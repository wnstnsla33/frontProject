package pProject.pPro.post;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.UserService;
import pProject.pPro.bookmark.BookmarkRepository;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostServiceDTO;
import pProject.pPro.post.DTO.WritePostDTO;

@Service
@Transactional
public class PostService {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BookmarkRepository bookmarkRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private PostMapper postMapper;

	public PostServiceDTO<List<PostListDTO>> getPostList() {
		List<PostEntity> postEntities = postRepository.findAll();
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new).collect(Collectors.toList());
		return new PostServiceDTO<>(true, "전체 게시글 조회 성공", postList);
	}

	public PostServiceDTO<List<PostListDTO>> getPostList(int page, int sortNumber) {
		int pageSize = 10;
		Pageable pageable = null;
		switch (sortNumber) {
		case 1 -> pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
		case 2 -> pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "viewCount"));
		case 3 -> pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "bookmarkCount"));
		}
		Page<PostEntity> postEntities = postRepository.findAll(pageable);
		System.out.println(postEntities);
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new).collect(Collectors.toList());
		return new PostServiceDTO<>(true, "페이지별 게시글 조회 성공", postList);
	}

	public PostServiceDTO<List<PostListDTO>> getPostBookmarkList(String email, int page, int sortNumber) {
		int pageSize = 10;
		Pageable pageable = null;
		switch (sortNumber) {
		case 1 -> pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
		case 2 -> pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.viewCount"));
		case 3 -> pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.bookmarkCount"));
		}
		Page<BookmarkEntity> bookmarkEntities = bookmarkRepository.bookmarkListByUser(email, pageable);
		List<PostListDTO> postList = bookmarkEntities.stream().map(PostListDTO::new).collect(Collectors.toList());
		return new PostServiceDTO<>(true, "북마크 게시글 조회 성공", postList);
	}

	public PostServiceDTO<Long> postCount() {
		return new PostServiceDTO<>(true, "전체 게시글 수 반환", postRepository.count());
	}

	public PostServiceDTO<Long> postBookmarkCount(String email) {
		return new PostServiceDTO<>(true, "북마크 수 반환", bookmarkRepository.countMyBookmark(email));
	}

	public PostServiceDTO<String> writePost(WritePostDTO writePostDTO, String email) {
		UserEntity user = userRepository.findByEmail(email).get();
		PostEntity newPost = new PostEntity(writePostDTO, user);
		if (writePostDTO.getSecreteKey() != null)
			newPost.setSecreteKey(passwordEncoder.encode(writePostDTO.getSecreteKey()));
		try {
			postRepository.save(newPost);
			return new PostServiceDTO<>(true, "정상적으로 등록되었습니다", null);
		} catch (Exception e) {
			return new PostServiceDTO<String>(false, "등록에 실패하였습니다.", null);
		}
	}

	public PostServiceDTO<PostEntity> incrementBookmarkCount(Long postId, boolean isBookmarked) {
		PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
		if (isBookmarked)
			post.setBookmarkCount(post.getBookmarkCount() + 1);
		else
			post.setBookmarkCount(post.getBookmarkCount() - 1);
		return new PostServiceDTO<>(true, "북마크 카운트 갱신", post);
	}

	public PostServiceDTO<PostListDTO> incrementAndGetPost(Long postId, String email) {
		PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글 없음"));
		post.setViewCount(post.getViewCount() + 1);
		PostListDTO postListDTO = new PostListDTO(post);
		if (email != null) {
			Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
			bookmark.ifPresent(b -> postListDTO.setBookmarked(true));
		}
		return new PostServiceDTO<>(true, "게시글 상세 조회", postListDTO);
	}

	public PostServiceDTO<PostListDTO> updatePost(Long postId, PostListDTO postListDTO, String email) {
		PostEntity post = postRepository.findById(postId).get();
		UserEntity user = userRepository.findByEmail(email).get();
		if (!post.getUser().getUserId().equals(user.getUserId())) {
			return new PostServiceDTO<>(false, "작성자만 수정할 수 있습니다", null);
		}
		post.setContent(postListDTO.getContent());
		post.setModifiedDate(LocalDateTime.now());
		post.setTitle(postListDTO.getTitle());
		PostListDTO updatePost = new PostListDTO(post);
		updatePost.setBookmarked(postListDTO.getBookmarked());
		return new PostServiceDTO<>(true, "수정 완료", updatePost);
	}

	public PostServiceDTO<String> deletePost(Long postId, String email) {
		PostEntity post = postRepository.findById(postId).get();
		UserEntity user = userRepository.findByEmail(email).get();
		if (!post.getUser().getUserId().equals(user.getUserId())) {
			return new PostServiceDTO<>(false, "작성자만 삭제할 수 있습니다", null);
		} else {
			postRepository.delete(post);
			return new PostServiceDTO<>(true, "삭제되었습니다", null);
		}
	}

	public PostServiceDTO<PostListDTO> getSecretePost(Long postId, String pwd) {
		PostEntity post = postRepository.findById(postId).get();
		if (passwordEncoder.matches(pwd, post.getSecreteKey())) {
			return new PostServiceDTO<>(true, "비밀번호 확인 완료", new PostListDTO(post, true));
		} else {
			return new PostServiceDTO<>(false, "비밀번호가 틀렸습니다", null);
		}
	}

	public PostServiceDTO<List<PostListDTO>> getMyPostList(String email) {
		List<PostEntity> postEntities = postRepository.getMyPostList(email);
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new).collect(Collectors.toList());
		return new PostServiceDTO<>(true, "내가 쓴 글 목록 조회", postList);
	}

	public PostServiceDTO<String> findPostTest(Long postId) {
		PostEntity post = postMapper.getPostDetail(postId);
		System.out.println(post.getContent());
		System.out.println(post.getSecreteKey());
		return new PostServiceDTO<>(true, "테스트 완료", null);
	}

	public PostServiceDTO<List<PostListDTO>> getTop10Posts() {
		List<PostListDTO> list = postRepository.findTop10ByOrderByViewCountDesc().stream().map(PostListDTO::new) // 또는
				.collect(Collectors.toList());
		return new PostServiceDTO<List<PostListDTO>>(true, "조회수 탑10입니다.", list);
	}

	public PostServiceDTO<List<PostListDTO>> getNoticeList(){
		List<PostListDTO> list = postRepository.getNoticeList().stream().map(PostListDTO::new) // 또는
				.collect(Collectors.toList());
		return new PostServiceDTO<List<PostListDTO>>(true, "게시판 목록입니다.", list);
	}
	
	public PostServiceDTO<PostEntity> getRecentNotice(){
		return new PostServiceDTO<PostEntity>(true, "첫번째 공지사항입니다.", postRepository.findTopByAdminOrderByCreatedAtDesc());
	}

}
