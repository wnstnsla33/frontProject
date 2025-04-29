package pProject.pPro.smtp;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService implements MailServiceInter {
	@Autowired
	JavaMailSender emailSender;
	
	private final RedisUtil redisUtil;
	private String ePw;

	@Override
	public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
		//레디스에 값있으면 삭제
		if(redisUtil.existData(to))redisUtil.deleteData(to);
		
		MimeMessage message = emailSender.createMimeMessage();

		message.addRecipients(RecipientType.TO, to); // 메일 받을 사용자
		message.setSubject("[Fligent] 비밀번호 변경을 위한 이메일 인증코드 입니다"); // 이메일 제목

		String msgg = "";
		msgg += "<h1>안녕하세요</h1>";
		msgg += "<h1>원하는 게시판 입니다</h1>";
		msgg += "<br>";
		msgg += "<p>아래 인증코드 입니다</p>";
		msgg += "<br>";
		msgg += "<br>";
		msgg += "<div align='center' style='border:1px solid black'>";
		msgg += "<h3 style='color:blue'>회원가입 인증코드 입니다</h3>";
		msgg += "<div style='font-size:130%'>";
		msgg += "<strong>" + ePw + "</strong></div><br/>"; // 메일에 인증번호 ePw 넣기
		msgg += "</div>";

		message.setText(msgg, "utf-8", "html");
		message.setFrom(new InternetAddress("wnstnsla3968@naver.com", "post_Admin"));
		return message;
	}

	@Override
	public String createKey() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		String key = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		return key;
	}

	@Override
	public void sendSimpleMessage(String to) throws Exception {
		ePw = createKey(); // 랜덤 인증코드 생성

		MimeMessage message = createMessage(to); // "to" 로 메일 발송

		redisUtil.setDataExpire(to, ePw, 10*60L);
		try { // 예외처리
			emailSender.send(message);
		} catch (Exception e) {
			throw new SmtpException(SmtpErrorCode.SMTP_ERROR);
		}
	}

	@Override
	public void authValid(String to, String code) {
		String validCode = redisUtil.getData(to);
		if(validCode==null) throw new SmtpException(SmtpErrorCode.REQUIRED_SMTP);
		else if(code.equals(validCode));
		else  throw new SmtpException(SmtpErrorCode.UNVALID_SMTP);
	}
	
}
