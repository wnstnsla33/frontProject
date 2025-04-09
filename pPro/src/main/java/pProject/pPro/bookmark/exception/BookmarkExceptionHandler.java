package pProject.pPro.bookmark.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;
import pProject.pPro.chat.exception.ChatException;
import pProject.pPro.chat.exception.ChatExceptionHandler;
@RestControllerAdvice
@Slf4j
public class BookmarkExceptionHandler {
	@ExceptionHandler(BookmarkException.class)
    public ResponseEntity<CommonResponse<Void>> handleChatException(ChatException e) {
        log.warn("ChatException 발생: {} - {}", e.getErrorCode(), e.getMessage());

        String message = switch (e.getErrorCode()) {
            case NOT_FOUND_CHAT -> "채팅을 찾을 수 없습니다.";
            case UNKNOWN_ERROR -> "알 수 없는 오류가 발생했습니다.";
            default -> "요청 처리 중 오류가 발생했습니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case NOT_FOUND_CHAT -> HttpStatus.NOT_FOUND;
            case UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }

}