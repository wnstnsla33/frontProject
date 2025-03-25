package pProject.pPro.post;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
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
import pProject.pPro.post.DTO.PostResponseDTO;
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
	
	private PostResponseDTO postResponseDTO;

	public List<PostListDTO> getPostList() {
		List<PostEntity> postEntities = postRepository.findAll();
		// Entity → DTO 변환
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new) // PostListDTO 생성자 사용
				.collect(Collectors.toList());
		return postList;
	}

	public List<PostListDTO> getPostList(int page, int sortNumber) {
		int pageSize = 10;
		Pageable pageable = null;
		switch (sortNumber) {
		case 1:
			pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
			break;
		case 2:
			pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "viewCount"));
			break;
		case 3:
			pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "bookmarkCount"));
			break;
		}
		// Pageable 객체 생성 (createDate 기준 내림차순 정렬)

		// 페이징 처리된 게시글 리스트 가져오기
		Page<PostEntity> postEntities = postRepository.findAll(pageable);

		// Entity → DTO 변환
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new).collect(Collectors.toList());
		postList.size();
		return postList;
	}

	public List<PostListDTO> getPostBookmarkList(String email, int page, int sortNumber) {
		int pageSize = 10;
		Pageable pageable = null;
		switch (sortNumber) {
		case 1: {
			pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
			break;
		}
		case 2: {
			pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.viewCount"));
			break;
		}
		case 3: {
			pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "post.bookmarkCount"));
			break;
		}
		}
		Page<BookmarkEntity> bookmarkEntities = bookmarkRepository.bookmarkListByUser(email, pageable);
		List<PostListDTO> postList = bookmarkEntities.stream().map(bookmark -> new PostListDTO(bookmark)) // 생성자 그대로 사용
				.collect(Collectors.toList());
		return postList;
	}

	public Long postCount() {
		return postRepository.count();
	}

	public Long postBookmarkCount(String email) {
		return bookmarkRepository.countMyBookmark(email);
	}

	public ResponseEntity writePost(WritePostDTO writePostDTO, String email) {
		UserEntity user = userRepository.findByEmail(email).get();
		PostEntity newPost = new PostEntity(writePostDTO, user);
		if (writePostDTO.getSecreteKey() != null)
			newPost.setSecreteKey(passwordEncoder.encode(writePostDTO.getSecreteKey()));
		postRepository.save(newPost);
		return postResponseDTO.postSuccess("정상적으로 등록되었습니다");
	}

//	public ResponseEntity getPostDetail(Long postId) {
//		return postResponseDTO.getPost(new PostListDTO((postRepository.getPostDetail(postId).orElseThrow(
//				() -> new RuntimeException("게시글이 존재하지 않습니다.")))));
//	}
	public PostEntity incrementBookmarkCount(Long postId, boolean isBookmarked) {
		PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
		if (isBookmarked)
			post.setBookmarkCount(post.getBookmarkCount() + 1);
		else
			post.setBookmarkCount(post.getBookmarkCount() - 1);
		return post;
	}

	public PostListDTO incrementAndGetPost(Long postId, String email) {
		PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글 없음"));
		post.setViewCount(post.getViewCount() + 1);
		PostListDTO postListDTO = new PostListDTO(post);
		if (email == null) {
			System.out.println("postService 로그인 X");
			return postListDTO;
		} else {
			Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
			if (bookmark.isPresent())
				postListDTO.setBookmarked(true);
		}
		return postListDTO;
	}

	public PostListDTO updatePost(Long postId, PostListDTO postListDTO, String email) {
		PostEntity post = postRepository.findById(postId).get();
		UserEntity user = userRepository.findByEmail(email).get();
		if (post.getUser().getUserId() != user.getUserId())
			return null;
		else {
			post.setContent(postListDTO.getContent());
			post.setModifiedDate(LocalDate.now());
			post.setTitle(postListDTO.getTitle());
			post.setTitleImg(postListDTO.getTitleImg());
		}
		PostListDTO updatePost = new PostListDTO(post);
		updatePost.setBookmarked(postListDTO.getBookmarked());
		return updatePost;
	}

	public boolean deletePost(Long postId, String email) {
		PostEntity post = postRepository.findById(postId).get();
		UserEntity user = userRepository.findByEmail(email).get();
		if (post.getUser().getUserId() != user.getUserId())
			return false;
		else {
			postRepository.delete(post);
			return true;
		}
	}

	public PostListDTO getSecretePost(Long postId, String pwd) {
		PostEntity post = postRepository.findById(postId).get();
		if (passwordEncoder.matches(pwd, post.getSecreteKey())) {
			return new PostListDTO(post,1);
		} else
			return null;
	}

	public List<PostListDTO> getMyPostList(String email) {
		List<PostEntity> postEntities = postRepository.getMyPostList(email);
		// Entity → DTO 변환
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new) // PostListDTO 생성자 사용
				.collect(Collectors.toList());
		return postList;
	}
	public void findPostTest(Long postId) {
		PostEntity post =  postMapper.getPostDetail(postId);
		System.out.println(post.getContent());
		System.out.println(post.getSecreteKey());
	}
}
