package pProject.pPro.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import pProject.pPro.EntityUtils;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.Exception.UserErrorCode;
import pProject.pPro.User.Exception.UserException;
import pProject.pPro.bookmark.BookmarkRepository;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.WritePostDTO;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PostMapper postMapper;
    private final EntityUtils utils;
    public List<PostListDTO> getPostList(String email, int page, int sortNumber, String keyword) {
        int pageSize = 10;
        Pageable pageable = switch (sortNumber) {
            case 1 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
            case 2 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "viewCount"));
            case 3 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "bookmarkCount"));
            default -> PageRequest.of(page, pageSize);
        };

        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Page<PostListDTO> postList = postRepository.findPostsWithBookmarkInfo(user.get().getUserId(), keyword, pageable);
            return postList.getContent();
        }
        Page<PostEntity> postList = postRepository.findPosts(keyword, pageable);
        return postList.getContent().stream().map(PostListDTO::new).toList();
    }

    public List<PostListDTO> getPostBookmarkList(String email, int page, int sortNumber) {
        int pageSize = 10;
        Pageable pageable = switch (sortNumber) {
            case 1 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
            case 2 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.viewCount"));
            case 3 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.bookmarkCount"));
            default -> PageRequest.of(page, pageSize);
        };
        Page<PostListDTO> postList = bookmarkRepository.bookmarkListByUser(email, pageable);
        return postList.getContent();
    }

    public void writePost(WritePostDTO writePostDTO, String email) {
        UserEntity user = utils.findUser(email);
        PostEntity newPost = new PostEntity(writePostDTO, user);
        if (writePostDTO.getSecreteKey() != null) {
            newPost.setSecreteKey(passwordEncoder.encode(writePostDTO.getSecreteKey()));
        }
        postRepository.save(newPost);
    }

    public PostEntity incrementBookmarkCount(Long postId, boolean isBookmarked) {
        PostEntity post = utils.findPost(postId);
        if (isBookmarked)
            post.setBookmarkCount(post.getBookmarkCount() + 1);
        else
            post.setBookmarkCount(post.getBookmarkCount() - 1);
        return post;
    }

    public PostListDTO incrementAndGetPost(Long postId, String email) {
        PostEntity post = utils.findPost(postId);
        post.setViewCount(post.getViewCount() + 1);
        PostListDTO dto = new PostListDTO(post);
        if (email != null) {
            bookmarkRepository.findBookmark(postId, email).ifPresent(b -> dto.setBookmarked(true));
        }
        return dto;
    }

    public PostListDTO updatePost(Long postId, PostListDTO postListDTO, String email) {
        PostEntity post = utils.findPost(postId);
        UserEntity user = utils.findUser(email);
        if (!post.getUser().getUserId().equals(user.getUserId()))
            throw new PostException(PostErrorCode.WRITER_ONLY);
        post.setContent(postListDTO.getContent());
        post.setModifiedDate(LocalDateTime.now());
        post.setTitle(postListDTO.getTitle());
        PostListDTO updated = new PostListDTO(post);
        updated.setBookmarked(postListDTO.isBookmarked());
        return updated;
    }

    public void deletePost(Long postId, String email) {
        PostEntity post = utils.findPost(postId);
        UserEntity user = utils.findUser(email);
        if (!post.getUser().getUserId().equals(user.getUserId()))
            throw new PostException(PostErrorCode.WRITER_ONLY);
        postRepository.delete(post);
    }

    public PostListDTO getSecretePost(Long postId, String pwd, String email) {
		PostEntity post = utils.findPost(postId);
		if (passwordEncoder.matches(pwd, post.getSecreteKey())) {
			if (email != null)
				return new PostListDTO(post, false, true);
			Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
			if (bookmark.isPresent())
				return  new PostListDTO(post, true, true);
			else
				return new PostListDTO(post, false, true);
		} else {
			throw new PostException(PostErrorCode.INVALID_PWD);
		}
	}

    public List<PostListDTO> getMyPostList(String email) {
        Long id = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL))
                .getUserId();
        List<PostEntity> postEntities = postRepository.getMyPostList(id);
        return postEntities.stream()
                .map(post -> {
                    boolean isBookmarked = post.getBookmark().stream().anyMatch(b -> b.getUser().getUserId().equals(id));
                    return new PostListDTO(post, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    public List<PostListDTO> getTop10Posts(String email) {
        PageRequest top10 = PageRequest.of(0, 10);
        if (email == null) {
            return postRepository.findTop10ByViewCount(top10);
        }
        UserEntity user = utils.findUser(email);
        return postRepository.findTop10ByViewCount(user.getUserId(), top10);
    }

    public List<PostListDTO> getNoticeList(String email) {
        if (email == null) {
            return postRepository.getNoticeList().stream().map(PostListDTO::new).collect(Collectors.toList());
        }
        UserEntity user = utils.findUser(email);
        return postRepository.getNoticeList(user.getUserId());
    }

   
}
