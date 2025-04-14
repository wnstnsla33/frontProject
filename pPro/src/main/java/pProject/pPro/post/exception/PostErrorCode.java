package pProject.pPro.post.exception;

import pProject.pPro.global.BaseErrorCode;

public enum PostErrorCode implements BaseErrorCode {
    POST_NOT_FOUND("해당 게시글이 존재하지 않습니다.", 404),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", 500),
    WRITER_ONLY("작성자만 수정 또는 삭제할 수 있습니다.", 403),
    INVALID_PWD("비밀번호가 일치하지 않습니다.", 401);

    private final String message;
    private final int status;

    PostErrorCode(String message, int status) {
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
