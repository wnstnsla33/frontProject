package pProject.pPro.reply;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import pProject.pPro.CommonResponse;
import pProject.pPro.ControllerUtils;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.reply.DTO.ReplyListDTO;
import pProject.pPro.reply.DTO.ReplyRegDTO;

@RestController
@RequiredArgsConstructor
public class ReplyController {

	private final ReplyService replyService;
	private final ControllerUtils utils;
	
	@PostMapping("/post/{postId}/reply")
	public ResponseEntity<?> createReply(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails user,
			@RequestBody ReplyRegDTO replyRegDTO) {
		System.out.println("댓글생성");
		ReplyListDTO result = replyService.saveReply(postId, replyRegDTO, utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("댓글이 등록되었습니다.", result));
	}

	@GetMapping("/post/{postId}/reply")
	public ResponseEntity<?> findReplyList(@PathVariable("postId") Long postId) {
		System.out.println("댓글보기");
		System.out.println("UserException 클래스: " + UserException.class.getClassLoader());
		List<ReplyListDTO> replyList = replyService.findReplyByPost(postId);
		return ResponseEntity.ok(CommonResponse.success("댓글 목록 조회 성공", replyList));
	}

	@DeleteMapping("/post/{postId}/{replyId}")
	public ResponseEntity<?> deleteReply(@PathVariable("postId") Long postId, @PathVariable("replyId") Long replyId,
			@AuthenticationPrincipal UserDetails user) {
		System.out.println("댓글지우기");
		replyService.deleteReply(postId, replyId, utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("댓글이 삭제되었습니다."));
	}

	@PutMapping("/post/{replyId}/reply")
	public ResponseEntity<?> updateReply(@PathVariable("replyId") Long replyId,
			@AuthenticationPrincipal UserDetails user, @RequestBody ReplyRegDTO replyRegDTO) {
		System.out.println("댓글 수정하기");
		ReplyListDTO updated = replyService.updateReply(replyId, replyRegDTO.getContent(), utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("댓글이 수정되었습니다.", updated));
	}
}
