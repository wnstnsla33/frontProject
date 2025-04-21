package pProject.pPro.smtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.global.CommonResponse;

@RestController
@Slf4j
public class MailServiceRestController {

	@Autowired
	MailService mailService;
	@GetMapping("/signup/confirm")
	public ResponseEntity mailConfirm(@RequestParam(name="email")String email)throws Exception{
		log.info("***********************************메일 보냄 경로 /signup/confirm");
		mailService.sendSimpleMessage(email);
		return ResponseEntity.ok(CommonResponse.success("이메일을 확인해주세요"));
	}
	@PostMapping("/signup/confirm")
	public ResponseEntity checkAuthcode(@RequestBody codeDTO codeDTO){
		mailService.authValid(codeDTO.getEmail(), codeDTO.getAuthcode());
		return ResponseEntity.status(HttpStatus.OK).body("인증코드가 확인돼었습니다.");
	}
}
