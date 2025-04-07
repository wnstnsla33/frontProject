package pProject.pPro.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class ChatExceptionHandler {

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<CommonResponse<Void>> handleChatException(ChatException e) {
        log.warn("ChatException 발생: {} - {}", e.getErrorCode(), e.getMessage());

        String message = switch (e.getErrorCode()) {
            case "NOT_FOUND_CHAT" -> "채팅을 찾을 수 없습니다.";
            case "UNKNOWN_ERROR" -> "알 수 없는 오류가 발생했습니다.";
            default -> "요청 처리 중 오류가 발생했습니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case "NOT_FOUND_CHAT" -> HttpStatus.NOT_FOUND;
            case "UNKNOWN_ERROR" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleUnknown(Exception e) {
        log.error("알 수 없는 채팅 예외 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail("서버 오류입니다. 잠시 후 다시 시도해주세요."));
    }
}
