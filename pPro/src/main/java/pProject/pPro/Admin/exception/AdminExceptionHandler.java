package pProject.pPro.Admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;
import pProject.pPro.reply.exception.ReplyException;

@RestControllerAdvice
@Slf4j
public class AdminExceptionHandler {
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<CommonResponse<Void>> handleEtc(Exception e) {
	        log.error("Reply 처리 중 알 수 없는 예외 발생", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(CommonResponse.fail("서버 오류입니다. 잠시 후 다시 시도해주세요."));
	    }
}
