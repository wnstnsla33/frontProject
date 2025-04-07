package pProject.pPro.securityConfig.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(PostNotFoundException.class )
	public ResponseEntity<?> handlePostNotFound(PostNotFoundException e){
		log.error("에러 발생[]",e.getMessage(),e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 게시물이 없습니다.");
	}
}