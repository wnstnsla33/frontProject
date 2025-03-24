package pProject.pPro.reply;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pProject.pPro.reply.DTO.ReplyListDTO;
import pProject.pPro.reply.DTO.ReplyRegDTO;
import pProject.pPro.reply.DTO.ReplyResponseDTO;

@RestController
public class ReplyController {

	@Autowired
	private ReplyService replyService;

	@PostMapping("/post/{postId}/reply")
	public ResponseEntity createReply(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails user,
			@RequestBody ReplyRegDTO replyRegDTO) {
		System.out.println("댓글생성");
		ReplyServiceValue<ReplyListDTO> result = replyService.saveReply(postId, replyRegDTO, user.getUsername());
		if (result.getEnumVal() == ReplyServiceEnum.SUCCESS)
			return ReplyResponseDTO.replyList(result.getData());
		else
			return ReplyResponseDTO.replySuccess();
	}
	@GetMapping("/post/{postId}/reply")
	public ResponseEntity findReplyList(@PathVariable("postId") Long postId) {
		System.out.println("댓글보기");
		List<ReplyListDTO> replyList= replyService.findReplyByPost(postId);
		return new ReplyResponseDTO().replyList(replyList);
	}
	@DeleteMapping("/post/{replyId}/reply")
	public ResponseEntity deleteReply(@PathVariable("replyId")Long replyId ,@AuthenticationPrincipal UserDetails user) {
		System.out.println("댓글지우기");
		ReplyServiceValue<String> result =  replyService.deleteReply(replyId, user.getUsername());
		if(result.getEnumVal()==ReplyServiceEnum.SUCCESS) return ReplyResponseDTO.replySuccess();
		else if(result.getEnumVal()==ReplyServiceEnum.EMAIL_NOTMATCH)return ReplyResponseDTO.replyFail("삭제할 권한이 없습니다");
		else return ReplyResponseDTO.replyFail("잘못된 요청입니다");
	}
	@PutMapping("/post/{replyId}/reply")
	public ResponseEntity updateReply(@PathVariable("replyId")Long replyId,@AuthenticationPrincipal UserDetails user
			,@RequestBody ReplyRegDTO replyRegDTO) {
		System.out.println("댓글 수정하기");
		ReplyServiceValue<ReplyListDTO> result = replyService.updateReply(replyId,replyRegDTO.getContent(), user.getUsername());
		if(result.getEnumVal()==ReplyServiceEnum.SUCCESS) return ReplyResponseDTO.replyList(result.getData());
		else if(result.getEnumVal()==ReplyServiceEnum.EMAIL_NOTMATCH)return ReplyResponseDTO.replyFail("수정할 권한이 없습니다");
		else return ReplyResponseDTO.replyFail("잘못된 요청입니다");
	}
}
