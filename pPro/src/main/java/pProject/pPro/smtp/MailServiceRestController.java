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

@RestController
@Slf4j
public class MailServiceRestController {

	@Autowired
	MailService mailService;
	@GetMapping("/signup/confirm")
	public ResponseEntity<EmailAuthResponseDTO> mailConfirm(@RequestParam(name="email")String email)throws Exception{
		log.info("***********************************메일 보냄 경로 /signup/confirm");
		EmailAuthResponseDTO response = mailService.sendSimpleMessage(email);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@PostMapping("/signup/confirm")
	public ResponseEntity<?> checkAuthcode(@RequestBody codeDTO codeDTO){
		EmailAuthResponseDTO response =  mailService.authValid(codeDTO.getEmail(), codeDTO.getAuthcode());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
