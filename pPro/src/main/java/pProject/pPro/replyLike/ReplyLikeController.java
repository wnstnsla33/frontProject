package pProject.pPro.replyLike;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.global.CommonResponse;
import pProject.pPro.global.ControllerUtils;
import pProject.pPro.replyLike.dto.ReplyLikeResponseDTO;

@RestController
@RequiredArgsConstructor
public class ReplyLikeController {

	private final ReplyLikeService replyLikeService;
	private final ControllerUtils utils;
	
	@PostMapping("/reply/like/{replyId}")
	public ResponseEntity<CommonResponse> saveReplyLike(@PathVariable("replyId")Long replyId,@AuthenticationPrincipal UserDetails user){
		ReplyLikeResponseDTO dto = replyLikeService.saveReplyLike(utils.findEmail(user), replyId);
		return  ResponseEntity.ok(CommonResponse.success("좋아요 버튼을 누르셨습니다.",dto));
	}
	
}
