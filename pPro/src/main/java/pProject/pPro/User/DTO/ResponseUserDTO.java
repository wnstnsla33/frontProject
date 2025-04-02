package pProject.pPro.User.DTO;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import pProject.pPro.entity.UserEntity;

@Getter
public class ResponseUserDTO {
    private int status; // 상태 코드
    private String message; // 응답 메시지

    // 생성자
    // 데이터가 없는 경우
    

    // 로그인
    public static ResponseEntity loginSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("로그인 되었습니다.");
    }

    public static ResponseEntity loginFail() {
    	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 잘못되었습니다.");
    }

    public static ResponseEntity accountNotFound() {
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계정이 존재하지 않습니다.");
    }
    //로그아웃
    public static ResponseEntity logoutSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("로그아웃 되었습니다.");
    }
    

    //회원 가입
    public static ResponseEntity existId() {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하는 계정입니다.");
    }
    public static ResponseEntity signupSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("회원가입이 정상적으로 되었습니다.");
    }
    
    //User
    public static ResponseEntity userInfo(UserEntity user) {//내 정보
    	return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    public static ResponseEntity userDelete() {
    	return ResponseEntity.status(HttpStatus.OK).body("정상삭제 완료했습니다.");
    }
    public static ResponseEntity userDeleteFail() {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제에 실패하였습니다.");
    }
    public static ResponseEntity okInMsg(String msg) {//내 정보
    	return ResponseEntity.status(HttpStatus.OK).body(msg);
    }
    public static ResponseEntity failInMsg(String msg) {//내 정보
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
}
