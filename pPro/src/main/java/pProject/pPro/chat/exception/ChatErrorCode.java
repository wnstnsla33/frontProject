package pProject.pPro.chat.exception;

import pProject.pPro.global.BaseErrorCode;

public enum ChatErrorCode implements BaseErrorCode {
    NOT_FOUND_CHAT("채팅을 찾을 수 없습니다.", 404),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.", 500);

    private final String message;
    private final int status;

    ChatErrorCode(String message, int status) {
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
