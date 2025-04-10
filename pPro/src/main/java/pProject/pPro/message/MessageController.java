package pProject.pPro.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pProject.pPro.CommonResponse;
import pProject.pPro.ControllerUtils;
import pProject.pPro.message.DTO.MessageListDTO;
import pProject.pPro.message.DTO.MessageResponseDTO;
import pProject.pPro.message.DTO.SaveMessageDTO;

@RestController
@RequiredArgsConstructor
public class MessageController {
	private final ControllerUtils utils;
	private final MessageService messageService;
	@PostMapping("/messages")
	public ResponseEntity<?> saveMessage(@AuthenticationPrincipal UserDetails user,@RequestBody @Valid SaveMessageDTO dto){
		String email = utils.findEmail(user);
		messageService.save(dto,email);
		return ResponseEntity.ok(CommonResponse.success("메세지를 보냈습니다."));
	}
	@GetMapping("/messages/received")
	public ResponseEntity<?> getReceivedMessages(
	        @AuthenticationPrincipal UserDetails user,
	        @RequestParam(name = "keyword",required = false) String keyword,
	        @PageableDefault(size = 10) Pageable pageable) {
		String email = utils.findEmail(user);
	    MessageListDTO dto = messageService.getReceivedMessages(email, keyword, pageable);
	    return ResponseEntity.ok(CommonResponse.success("받은 메세지들입니다.", dto));
	}

	@GetMapping("/messages/sent")
	public ResponseEntity<?> getSentMessages(
	        @AuthenticationPrincipal UserDetails user,
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @PageableDefault(size = 10) Pageable pageable) {
		String email = utils.findEmail(user);
	    MessageListDTO dto = messageService.getSentMessages(email, keyword, pageable);
	    return ResponseEntity.ok(CommonResponse.success("보낸 메세지들입니다.", dto));
	}

	@GetMapping("/messages/unreadMsg")
	public ResponseEntity getMsgCount(@AuthenticationPrincipal UserDetails detail) {
		String email = utils.findEmail(detail);
		return ResponseEntity.ok(CommonResponse.success("안읽은 메세지수 입니다.",messageService.unreadMessageCount(email)));
	}
	
	@GetMapping("/messages/{messageId}")
	public ResponseEntity detailMsg(@PathVariable("messageId")Long messageId) {
		return ResponseEntity.ok(CommonResponse.success("메세지 상세 입니다.",messageService.messageDetail(messageId)));
	}
}
