package pProject.pPro.User.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {
	@ExceptionHandler(UserException.class)
	public ResponseEntity<CommonResponse<Void>> handleUserException(UserException e) {
	    log.warn("UserException 발생: {}", e.getErrorCode());

	    // customMessage가 있으면 우선 사용
	    String message = (e.getCustomMessage() != null) ? e.getCustomMessage() : switch (e.getErrorCode()) {
	        case EXIST_ID -> "이미 존재하는 아이디입니다.";
	        case NO_EXIST_ID, INVALID_ID -> "존재하지 않는 아이디입니다.";
	        case INVALID_EMAIL -> "잘못된 이메일 형식입니다.";
	        case INVALID_PASSWORD -> "비밀번호가 잘못됐습니다.";
	        case INVALID_BIRTH_DAY -> "생일 정보가 일치하지 않습니다.";
	        case INVALID_NAME -> "이름이 일치하지 않습니다.";
	        case ISSOCIAL -> "소셜 계정은 해당 기능을 사용할 수 없습니다.";
	        case UNKNOWN -> "서버 오류입니다. 잠시 후 다시 시도해주세요.";
	        case REQUIRED_LOGIN ->"로그인이 필요한 서비스입니다.";
	    };

	    HttpStatus status = switch (e.getErrorCode()) {
	        case EXIST_ID -> HttpStatus.CONFLICT;
	        case NO_EXIST_ID, INVALID_ID, INVALID_EMAIL -> HttpStatus.NOT_FOUND;
	        case INVALID_PASSWORD -> HttpStatus.UNAUTHORIZED;
	        case INVALID_BIRTH_DAY, INVALID_NAME, ISSOCIAL -> HttpStatus.BAD_REQUEST;
	        default -> HttpStatus.INTERNAL_SERVER_ERROR;
	    };

	    return ResponseEntity.status(status)
	            .body(CommonResponse.fail(message));
	}


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.fail("접근 권한이 없습니다. (정지된 계정일 수 있습니다)"));
    }
}
