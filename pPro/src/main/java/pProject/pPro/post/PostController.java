package pProject.pPro.post;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pProject.pPro.User.UserService;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.CommonResponse;
import pProject.pPro.global.ControllerUtils;
import pProject.pPro.post.DTO.EditPostDTO;
import pProject.pPro.post.DTO.PassWordDTO;
import pProject.pPro.post.DTO.PostDetailWithReply;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostPageDTO;
import pProject.pPro.post.DTO.PostSearchDTO;
import pProject.pPro.post.DTO.WritePostDTO;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;
import pProject.pPro.reply.ReplyService;
import pProject.pPro.reply.DTO.ReplyListDTO;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final UserService userService;
	private final ControllerUtils utils;
	private final ReplyService replyService;
	@PostMapping("/post/new")
	public ResponseEntity<?> newPost(@ModelAttribute WritePostDTO writePostDTO,
	                                 @AuthenticationPrincipal UserDetails loginUser) {
		utils.isBannedUser(loginUser);
		String email = utils.findEmail(loginUser);
		UserEntity user= userService.expUp(email);
		postService.writePost(writePostDTO, user);
		return ResponseEntity.ok(CommonResponse.success("정상적으로 등록되었습니다", null));
	}

	@PostMapping("/post/image")
	public ResponseEntity<?> imageUpload(@RequestPart("image") MultipartFile imageFile) {
		if (imageFile.isEmpty()) {
			throw new PostException(PostErrorCode.UNKNOWN_ERROR);
		}
		String imageUrl = postService.saveImage(imageFile);
		return ResponseEntity.ok(CommonResponse.success("이미지 업로드 성공", imageUrl));
	}

	@GetMapping("/post")
	public ResponseEntity<?> postList(@ModelAttribute PostSearchDTO searchDTO,
	                                  @AuthenticationPrincipal UserDetails userDetails) {
		String email = utils.findEmailOrNull(userDetails);
		PostPageDTO list = postService.getPostList(
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
		PostPageDTO list = postService.getPostBookmarkList(utils.findEmail(user), page - 1, sortNumber);
		return ResponseEntity.ok(CommonResponse.success("북마크 게시글 조회 성공", list));
	}

	@GetMapping("/post/{postId}")
	public ResponseEntity<?> getPostDetail(@PathVariable("postId") long postId,
	                                       @AuthenticationPrincipal UserDetails loginUser) {
		String email = utils.findEmailOrNull(loginUser);
		PostListDTO dto = postService.incrementAndGetPost(postId, email);
		List<ReplyListDTO> replyList = replyService.findReplyByPost(postId,email);
		return ResponseEntity.ok(CommonResponse.success("게시글 상세 조회 성공", new PostDetailWithReply(dto,replyList)));
	}

	@PutMapping("/post/{postId}")
	public ResponseEntity<?> updatePost(@PathVariable("postId") long postId,
	                                    @RequestBody PostListDTO updatePost,
	                                    @AuthenticationPrincipal UserDetails loginUser) {
		PostListDTO updated = postService.updatePost(postId, updatePost, utils.findEmail(loginUser));
		return ResponseEntity.ok(CommonResponse.success("게시글 수정 완료", updated));
	}

	@DeleteMapping("/post/{postId}")
	public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,
	                                    @AuthenticationPrincipal UserDetails loginUser) {
		postService.deletePost(postId, utils.findEmail(loginUser));
		return ResponseEntity.ok(CommonResponse.success("삭제되었습니다", null));
	}

	@PostMapping("/post/secrete/{postId}")
	public ResponseEntity<?> getSecretePost(@PathVariable("postId") Long postId,
	                                        @RequestBody PassWordDTO passWordDTO,
	                                        @AuthenticationPrincipal UserDetails user) {
		String email = utils.findEmailOrNull(user);
		PostListDTO dto = postService.getSecretePost(postId, passWordDTO.getPwd(), email);
		List<ReplyListDTO> replyList = replyService.findReplyByPost(postId,email);
		return ResponseEntity.ok(CommonResponse.success("비밀 게시글 조회 성공", new PostDetailWithReply(dto, replyList)));
	}

	@GetMapping("/post/myPost")
	public ResponseEntity<?> getMyPostLists(@AuthenticationPrincipal UserDetails user) {
		List<PostListDTO> list = postService.getMyPostList(utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("내 게시글 조회 성공", list));
	}

	@GetMapping("/post/topView")
	public ResponseEntity<?> getTop10Post(@AuthenticationPrincipal UserDetails user) {
		String email = utils.findEmailOrNull(user);
		List<PostListDTO> list = postService.getTop10Posts(email);
		return ResponseEntity.ok(CommonResponse.success("TOP10 게시글 조회 성공", list));
	}

	@GetMapping("/post/notice")
	public ResponseEntity<?> getNoticeList(@AuthenticationPrincipal UserDetails user) {
		String email = utils.findEmailOrNull(user);
		List<PostListDTO> list = postService.getNoticeList(email);
		return ResponseEntity.ok(CommonResponse.success("공지 게시글 조회 성공", list));
	}
	@GetMapping("/post/edit/{postId}")
	public ResponseEntity<?> getPostEditInfo(@PathVariable("postId")Long postId, @AuthenticationPrincipal UserDetails user) {
		String email = utils.findEmail(user);
		EditPostDTO dto = postService.getEditPost(email, postId);
		return ResponseEntity.ok(CommonResponse.success("공지 게시글 조회 성공", dto));
	}
}
