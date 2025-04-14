package pProject.pPro.Report.exception;

import pProject.pPro.global.BaseErrorCode;

public enum ReportErrorCode implements BaseErrorCode {
    DUPLICATE_REPORT("이미 동일한 대상에 대해 신고가 접수되었습니다.", 409),
    INVALID_ID("신고 대상이 유효하지 않습니다.", 400);

    private final String message;
    private final int status;

    ReportErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
