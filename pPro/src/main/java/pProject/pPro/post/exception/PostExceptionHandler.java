package pProject.pPro.post.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class PostExceptionHandler {

    @ExceptionHandler(PostException.class)
    public ResponseEntity<CommonResponse<Void>> handlePostException(PostException e) {
        log.warn("PostException 발생: {}", e.getErrorCode());

        String message = (e.getCustomMessage() != null) ? e.getCustomMessage() : switch (e.getErrorCode()) {
            case POST_NOT_FOUND -> "해당 게시글이 존재하지 않습니다.";
            case WRITER_ONLY -> "작성자만 수정 또는 삭제할 수 있습니다.";
            case INVALID_PWD -> "비밀번호가 일치하지 않습니다.";
            case UNKNOWN_ERROR -> "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case POST_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case WRITER_ONLY -> HttpStatus.FORBIDDEN;
            case INVALID_PWD -> HttpStatus.UNAUTHORIZED;
            case UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }

    // 그 외 예외 (예: NullPointerException, IllegalArgumentException 등)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleEtcException(Exception e) {
        log.error("Post 처리 중 알 수 없는 예외 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail("서버 오류입니다. 잠시 후 다시 시도해주세요."));
    }
}
