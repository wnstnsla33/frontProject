package pProject.pPro.User.DTO;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

@Getter
public class ResponseUserDTO {
    private int status; // 상태 코드
    private String message; // 응답 메시지

    // 생성자
    // 데이터가 없는 경우
    

    // 로그인
    public static ResponseEntity<?> loginSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("로그인 되었습니다.");
    }

    public static ResponseEntity<?> loginFail() {
    	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 잘못되었습니다.");
    }

    public static ResponseEntity<?> accountNotFound() {
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계정이 존재하지 않습니다.");
    }

    

    //회원 가입
    public static ResponseEntity<?> existId() {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하는 계정입니다.");
    }
    public static ResponseEntity<?> signupSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("회원가입이 정상적으로 되었습니다.");
    }
}
