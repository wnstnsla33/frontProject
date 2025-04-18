package pProject.pPro.bookmark.exception;

import pProject.pPro.global.BaseErrorCode;

public enum BookmarkErrorCode implements BaseErrorCode {
    NOT_FOUND_BOOKMARK("북마크를 찾을 수 없습니다.", 404),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.", 500);

    private final String message;
    private final int status;

    BookmarkErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}