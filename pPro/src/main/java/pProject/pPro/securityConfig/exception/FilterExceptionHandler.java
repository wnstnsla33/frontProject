// pProject.pPro.securityConfig.exception.FilterExceptionHandler.java
package pProject.pPro.securityConfig.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class FilterExceptionHandler {

    @ExceptionHandler(FilterException.class)
    public ResponseEntity<CommonResponse<Void>> handleFilterException(FilterException e) {
        log.warn("FilterException 발생: {}", e.getErrorCode());

        // customMessage가 있으면 우선 사용
        String message = (e.getCustomMessage() != null) ? e.getCustomMessage() : switch (e.getErrorCode()) {
            case EXPIRED_ACCESS -> "Access 토큰이 만료되었습니다.";
            case EXPIRED_REFRESH -> "Refresh 토큰이 만료되었습니다.";
            case REQUIRED_LOGIN -> "로그인이 필요합니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case EXPIRED_ACCESS, EXPIRED_REFRESH -> HttpStatus.UNAUTHORIZED;
            case REQUIRED_LOGIN -> HttpStatus.FORBIDDEN;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }
}
