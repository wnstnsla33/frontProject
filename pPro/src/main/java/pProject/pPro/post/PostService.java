package pProject.pPro.post;

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

import pProject.pPro.ServiceUtils;
import pProject.pPro.bookmark.BookmarkRepository;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
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
        log.info("********** getPostList() 호출 - email: {}, page: {}, sortNumber: {}, keyword: '{}' **********", email, page, sortNumber, keyword);
        int pageSize = 10;
        Pageable pageable = switch (sortNumber) {
            case 1 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
            case 2 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "viewCount"));
            case 3 -> PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "bookmarkCount"));
            default -> PageRequest.of(page, pageSize);
        };

        Optional<UserEntity> user =  utils.findUserOptional(email);
        if (user.isPresent()) {
            log.info("📌 로그인 유저 기준 북마크 포함 게시글 조회");
            Page<PostListDTO> postList = postRepository.findPostsWithBookmarkInfo(user.get().getUserId(), keyword, pageable);
            return new PostPageDTO(postList.getContent(), postList.getTotalPages());
        }

        log.info("📌 비로그인 기준 게시글 조회");
        Page<PostEntity> postList = postRepository.findPosts(keyword, pageable);
        List<PostListDTO> list =  postList.getContent().stream().map(PostListDTO::new).toList();
        return new PostPageDTO(list, postList.getTotalPages());
    }

    public PostPageDTO getPostBookmarkList(String email, int page, int sortNumber) {
        log.info("********** getPostBookmarkList() 호출 - email: {}, page: {}, sortNumber: {} **********", email, page, sortNumber);
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
        log.info("********** writePost() 호출 - email: {}, title: '{}' **********",  writePostDTO.getTitle());
        PostEntity newPost = new PostEntity(writePostDTO, user);
        if (writePostDTO.getSecreteKey() != null) {
            log.info("🔐 비밀글 작성 - 시크릿 키 암호화");
            newPost.setSecreteKey(passwordEncoder.encode(writePostDTO.getSecreteKey()));
        }
        postRepository.save(newPost);
        log.info("✅ 게시글 저장 완료 - postId: {}", newPost.getPostId());
    }

    public PostListDTO incrementAndGetPost(Long postId, String email) {
        log.info("********** incrementAndGetPost() 호출 - postId: {}, email: {} **********", postId, email);
        PostEntity post = utils.findPost(postId);
        post.setViewCount(post.getViewCount() + 1);
        PostListDTO dto = new PostListDTO(post);
        if (email != null) {
            bookmarkRepository.findBookmark(postId, email).ifPresent(b -> dto.setBookmarked(true));
        }
        return dto;
    }

    public PostListDTO updatePost(Long postId, PostListDTO postListDTO, String email) {
        log.info("********** updatePost() 호출 - postId: {}, email: {} **********", postId, email);
        PostEntity post = utils.findPost(postId);
        UserEntity user = utils.findUser(email);
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            log.warn("🚫 게시글 수정 권한 없음 - 유저 ID 불일치");
            throw new PostException(PostErrorCode.WRITER_ONLY);
        }
        post.setContent(postListDTO.getContent());
        post.setModifiedDate(LocalDateTime.now());
        post.setTitle(postListDTO.getTitle());
        log.info("✅ 게시글 수정 완료 - postId: {}", postId);
        PostListDTO updated = new PostListDTO(post);
        updated.setBookmarked(postListDTO.isBookmarked());
        return updated;
    }

    public void deletePost(Long postId, String email) {
        log.info("********** deletePost() 호출 - postId: {}, email: {} **********", postId, email);
        PostEntity post = utils.findPost(postId);
        UserEntity user = utils.findUser(email);
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            log.warn("🚫 게시글 삭제 권한 없음 - 유저 ID 불일치");
            throw new PostException(PostErrorCode.WRITER_ONLY);
        }
        postRepository.delete(post);
        log.info("🗑️ 게시글 삭제 완료 - postId: {}", postId);
    }

    public PostListDTO getSecretePost(Long postId, String pwd, String email) {
        log.info("********** getSecretePost() 호출 - postId: {}, email: {} **********", postId, email);
        PostEntity post = utils.findPost(postId);
        if (passwordEncoder.matches(pwd, post.getSecreteKey())) {
            log.info("🔓 비밀글 비밀번호 일치");
            if (email != null)
                return new PostListDTO(post, false, true);
            Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
            if (bookmark.isPresent())
                return new PostListDTO(post, true, true);
            else
                return new PostListDTO(post, false, true);
        } else {
            log.warn("🚫 비밀글 비밀번호 불일치 - postId: {}", postId);
            throw new PostException(PostErrorCode.INVALID_PWD);
        }
    }

    public List<PostListDTO> getMyPostList(String email) {
        log.info("********** getMyPostList() 호출 - email: {} **********", email);
        Long id = utils.findUser(email).getUserId();
        List<PostEntity> postEntities = postRepository.getMyPostList(id);
        return postEntities.stream()
                .map(post -> {
                    boolean isBookmarked = post.getBookmark().stream()
                            .anyMatch(b -> b.getUser().getUserId().equals(id));
                    return new PostListDTO(post, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    public String saveImg(MultipartFile file) {
        log.info("********** saveImg() 호출 - 파일명: {} **********", file.getOriginalFilename());
        return utils.saveImage(file);
    }

    public List<PostListDTO> getTop10Posts(String email) {
        log.info("********** getTop10Posts() 호출 - email: {} **********", email);
        PageRequest top10 = PageRequest.of(0, 10);
        if (email == null) {
            log.info("📌 비로그인 유저 기준 인기글 조회");
            return postRepository.findTop10ByViewCount(top10);
        }
        UserEntity user = utils.findUser(email);
        return postRepository.findTop10ByViewCount(user.getUserId(), top10);
    }

    public List<PostListDTO> getNoticeList(String email) {
        log.info("********** getNoticeList() 호출 - email: {} **********", email);
        if (email == null) {
            return postRepository.getNoticeList().stream().map(PostListDTO::new).collect(Collectors.toList());
        }
        UserEntity user = utils.findUser(email);
        return postRepository.getNoticeList(user.getUserId());
    }

}
