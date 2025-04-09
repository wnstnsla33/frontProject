package pProject.pPro.Report.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class ReportExceptionHandler {

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<CommonResponse<Void>> handleReportException(ReportException e) {
        log.warn("📛 ReportException 발생: {}", e.getErrorCode());

        String message = (e.getMessage() != null) ? e.getMessage() : switch (e.getErrorCode()) {
            case DUPLICATE_REPORT -> "이미 동일한 대상에 대해 신고가 접수되었습니다.";
            case INVALID_ID -> "신고 대상이 유효하지 않습니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case DUPLICATE_REPORT -> HttpStatus.CONFLICT;
            case INVALID_ID -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }
}
