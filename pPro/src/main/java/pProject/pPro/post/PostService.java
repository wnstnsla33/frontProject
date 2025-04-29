package pProject.pPro.post;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import pProject.pPro.bookmark.BookmarkRepository;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.post.DTO.EditPostDTO;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostPageDTO;
import pProject.pPro.post.DTO.WritePostDTO;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ServiceUtils utils;

    public PostPageDTO getPostList(String email, int page, int sortNumber, String keyword) {
        int pageSize = 10;
        Pageable pageable = switch (sortNumber) {
            case 1 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
            case 2 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "viewCount"));
            case 3 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "bookmarkCount"));
            default -> PageRequest.of(page, pageSize);
        };

        Optional<UserEntity> user =  utils.findUserOptional(email);
        if (user.isPresent()) {
            Page<PostListDTO> postList = postRepository.findPostsWithBookmarkInfo(user.get().getUserId(), keyword, pageable);
            return new PostPageDTO(postList.getContent(), postList.getTotalPages());
        }

        Page<PostEntity> postList = postRepository.findPosts(keyword, pageable);
        List<PostListDTO> list =  postList.getContent().stream().map(PostListDTO::new).toList();
        return new PostPageDTO(list, postList.getTotalPages());
    }

    public PostPageDTO getPostBookmarkList(String email, int page, int sortNumber) {
        int pageSize = 10;
        Pageable pageable = switch (sortNumber) {
            case 1 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
            case 2 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.viewCount"));
            case 3 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.bookmarkCount"));
            default -> PageRequest.of(page, pageSize);
        };
        Page<PostListDTO> postList = bookmarkRepository.bookmarkListByUser(email, pageable);
        return new PostPageDTO(postList.getContent(), postList.getTotalPages());
    }

    public void writePost(WritePostDTO writePostDTO,UserEntity user) {
        PostEntity newPost = new PostEntity(writePostDTO, user);
        if (writePostDTO.getSecreteKey() != null) {
            newPost.setSecreteKey(passwordEncoder.encode(writePostDTO.getSecreteKey()));
        }
        postRepository.save(newPost);
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
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new PostException(PostErrorCode.WRITER_ONLY);
        }
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
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new PostException(PostErrorCode.WRITER_ONLY);
        }
        postRepository.delete(post);
    }

    public PostListDTO getSecretePost(Long postId, String pwd, String email) {
        PostEntity post = utils.findPost(postId);
        if (passwordEncoder.matches(pwd, post.getSecreteKey())) {
            post.setViewCount(post.getViewCount()+1);
            if (email != null)
                return new PostListDTO(post, false, true);
            Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
            if (bookmark.isPresent())
                return new PostListDTO(post, true, true);
            else
                return new PostListDTO(post, false, true);
        } else {
            throw new PostException(PostErrorCode.INVALID_PWD);
        }
    }

    public List<PostListDTO> getMyPostList(String email) {
        Long id = utils.findUser(email).getUserId();
        List<PostListDTO> postEntities = postRepository.findPostsWithBookmarkInfo(id);
        return postEntities;
    }


    public List<PostListDTO> getTop10Posts(String email) {
        Pageable pageable =PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "viewCount"));
        if (email == null) {
            return postRepository.findTop10ByViewCountFixed(pageable);
        }
        UserEntity user = utils.findUser(email);
        return postRepository.findTop10ByViewCount(user.getUserId(),pageable);
    }

    public List<PostListDTO> getNoticeList(String email) {
        if (email == null) {
            return postRepository.getNoticeList().stream().map(PostListDTO::new).collect(Collectors.toList());
        }
        UserEntity user = utils.findUser(email);
        return postRepository.getNoticeList(user.getUserId());
    }
    public EditPostDTO getEditPost(String email, Long postId) {
    	 PostEntity post = postRepository.getPostEditInfo(postId).orElseThrow(()->  new PostException(PostErrorCode.POST_NOT_FOUND));
    	 if(email.equals(post.getUser().getUserEmail()))return new EditPostDTO(post);
    	 throw new PostException(PostErrorCode.WRITER_ONLY);
    }

    public String saveImage(MultipartFile imageFile) {
    	String UPLOAD_DIR = "/home/ubuntu/uploads/";
		File dir = new File(UPLOAD_DIR);
		if (!dir.exists())
			dir.mkdirs();

		String originalFilename = imageFile.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String savedFileName = UUID.randomUUID() + extension;

		File savedFile = new File(UPLOAD_DIR + savedFileName);
		try {

			imageFile.transferTo(savedFile);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return "/uploads/" + savedFileName;
	}
}
