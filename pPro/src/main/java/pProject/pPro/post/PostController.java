package pProject.pPro.post;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pProject.pPro.CommonResponse;
import pProject.pPro.User.UserService;
import pProject.pPro.entity.ImageStorageService;
import pProject.pPro.post.DTO.PassWordDTO;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostSearchDTO;
import pProject.pPro.post.DTO.WritePostDTO;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final UserService userService;
	private final ImageStorageService imageStorageService;

	private void makeMessage(String methodName) {
		System.out.println("********************************" + methodName);
	}

	@PostMapping("/post/new")
	public ResponseEntity<?> newPost(@ModelAttribute WritePostDTO writePostDTO,
	                                 @AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("newPost");
		String email = loginUser.getUsername();
		userService.expUp(email);
		postService.writePost(writePostDTO, email);
		return ResponseEntity.ok(CommonResponse.success("정상적으로 등록되었습니다", null));
	}

	@PostMapping("/post/image")
	public ResponseEntity<?> imageUpload(@RequestPart("image") MultipartFile imageFile) {
		if (imageFile.isEmpty()) {
			throw new IllegalArgumentException("이미지 값이 없습니다.");
		}
		String imageUrl = imageStorageService.saveImage(imageFile);
		return ResponseEntity.ok(CommonResponse.success("이미지 업로드 성공", imageUrl));
	}

	@GetMapping("/post")
	public ResponseEntity<?> postList(@ModelAttribute PostSearchDTO searchDTO,
	                                  @AuthenticationPrincipal UserDetails userDetails) {
		String email = (userDetails != null) ? userDetails.getUsername() : null;
		List<PostListDTO> list = postService.getPostList(
				email,
				searchDTO.getPage() - 1,
				searchDTO.getSortType(),
				searchDTO.getKeyword()
		);
		return ResponseEntity.ok(CommonResponse.success("게시글 목록 조회 성공", list));
	}

	@GetMapping("/post/mybookmark")
	public ResponseEntity<?> bookmarkPostList(@AuthenticationPrincipal UserDetails user,
	                                          @RequestParam(value = "page", defaultValue = "0") int page,
	                                          @RequestParam(value = "sortType", defaultValue = "1") int sortNumber) {
		makeMessage("post/bookmark");
		List<PostListDTO> list = postService.getPostBookmarkList(user.getUsername(), page - 1, sortNumber);
		return ResponseEntity.ok(CommonResponse.success("북마크 게시글 조회 성공", list));
	}

	@GetMapping("/post/{postId}")
	public ResponseEntity<?> getPostDetail(@PathVariable("postId") long postId,
	                                       @AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("post/postId");
		String email = (loginUser != null) ? loginUser.getUsername() : null;
		PostListDTO dto = postService.incrementAndGetPost(postId, email);
		return ResponseEntity.ok(CommonResponse.success("게시글 상세 조회 성공", dto));
	}

	@PutMapping("/post/{postId}")
	public ResponseEntity<?> updatePost(@PathVariable("postId") long postId,
	                                    @RequestBody PostListDTO updatePost,
	                                    @AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("update post/postId");
		PostListDTO updated = postService.updatePost(postId, updatePost, loginUser.getUsername());
		return ResponseEntity.ok(CommonResponse.success("게시글 수정 완료", updated));
	}

	@DeleteMapping("/post/{postId}")
	public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,
	                                    @AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("delete post/postId");
		postService.deletePost(postId, loginUser.getUsername());
		return ResponseEntity.ok(CommonResponse.success("삭제되었습니다", null));
	}

	@PostMapping("/post/secrete/{postId}")
	public ResponseEntity<?> getSecretePost(@PathVariable("postId") Long postId,
	                                        @RequestBody PassWordDTO passWordDTO,
	                                        @AuthenticationPrincipal UserDetails user) {
		makeMessage("secrete post/postId" + postId + passWordDTO.getPwd());
		String email = (user != null) ? user.getUsername() : null;
		PostListDTO dto = postService.getSecretePost(postId, passWordDTO.getPwd(), email);
		return ResponseEntity.ok(CommonResponse.success("비밀 게시글 조회 성공", dto));
	}

	@GetMapping("/post/myPost")
	public ResponseEntity<?> getMyPostLists(@AuthenticationPrincipal UserDetails user) {
		makeMessage("내가 쓴 게시물 보기");
		List<PostListDTO> list = postService.getMyPostList(user.getUsername());
		return ResponseEntity.ok(CommonResponse.success("내 게시글 조회 성공", list));
	}

	@GetMapping("/post/topView")
	public ResponseEntity<?> getTop10Post(@AuthenticationPrincipal UserDetails user) {
		String email = (user != null) ? user.getUsername() : null;
		List<PostListDTO> list = postService.getTop10Posts(email);
		return ResponseEntity.ok(CommonResponse.success("TOP10 게시글 조회 성공", list));
	}

	@GetMapping("/post/notice")
	public ResponseEntity<?> getNoticeList(@AuthenticationPrincipal UserDetails user) {
		String email = (user != null) ? user.getUsername() : null;
		List<PostListDTO> list = postService.getNoticeList(email);
		return ResponseEntity.ok(CommonResponse.success("공지 게시글 조회 성공", list));
	}
}
