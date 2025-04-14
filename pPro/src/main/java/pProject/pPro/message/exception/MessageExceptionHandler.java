package pProject.pPro.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;
import pProject.pPro.post.exception.PostException;
@Slf4j
@RestControllerAdvice
public class MessageExceptionHandler {
	 @ExceptionHandler(MessageExeption.class)
	    public ResponseEntity<CommonResponse<Void>> handlePostException(MessageExeption e) {
	        log.warn("PostException 발생: {}", e.getErrorCode());

	        String message = (e.getCustomMessage() != null) ? e.getCustomMessage() : switch (e.getErrorCode()) {
	            case INVALID_USER_ID -> "잘못된 메세지 요청입니다.";
	            case INVIALD_ID -> "해당 메세지는 존재하지 않는 메세지입니다.";
	        };

	        HttpStatus status = switch (e.getErrorCode()) {
	            case INVALID_USER_ID -> HttpStatus.FORBIDDEN;
	            case INVIALD_ID -> HttpStatus.NOT_FOUND;
	        };

	        return ResponseEntity.status(status)
	                .body(CommonResponse.fail(message));
	    }

}
