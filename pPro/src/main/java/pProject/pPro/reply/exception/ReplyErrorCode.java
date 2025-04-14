package pProject.pPro.reply.exception;

import pProject.pPro.global.BaseErrorCode;

public enum ReplyErrorCode implements BaseErrorCode {
    REPLY_NOT_FOUND("해당 댓글이 존재하지 않습니다.", 404),
    PARENT_NOT_FOUND("부모 댓글을 찾을 수 없습니다.", 400),
    UNKNOWN("댓글 처리 중 알 수 없는 오류가 발생했습니다.", 500);

    private final String message;
    private final int status;

    ReplyErrorCode(String message, int status) {
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
