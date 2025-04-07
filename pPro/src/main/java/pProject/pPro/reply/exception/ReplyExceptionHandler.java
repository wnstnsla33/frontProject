package pProject.pPro.reply.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class ReplyExceptionHandler {

    @ExceptionHandler(ReplyException.class)
    public ResponseEntity<CommonResponse<Void>> handleReplyException(ReplyException e) {
        log.warn("ReplyException 발생: {}", e.getErrorCode());

        String message = (e.getCustomMessage() != null) ? e.getCustomMessage() : switch (e.getErrorCode()) {
            case REPLY_NOT_FOUND -> "해당 댓글이 존재하지 않습니다.";
            case PARENT_NOT_FOUND -> "부모 댓글을 찾을 수 없습니다.";
            case UNKNOWN -> "댓글 처리 중 알 수 없는 오류가 발생했습니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case REPLY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case PARENT_NOT_FOUND -> HttpStatus.BAD_REQUEST;
            case UNKNOWN -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }

    // 기타 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleEtc(Exception e) {
        log.error("Reply 처리 중 알 수 없는 예외 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail("서버 오류입니다. 잠시 후 다시 시도해주세요."));
    }
}
