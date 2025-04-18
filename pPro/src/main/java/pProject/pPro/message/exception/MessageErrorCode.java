package pProject.pPro.message.exception;

import pProject.pPro.global.BaseErrorCode;

public enum MessageErrorCode implements BaseErrorCode {
    INVIALD_ID("존재하지 않는 메시지입니다.", 404),
    INVALID_USER_ID("메시지에 접근할 수 있는 권한이 없습니다.", 403);

    private final String message;
    private final int status;

    MessageErrorCode(String message, int status) {
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
