package pProject.pPro.bookmark.exception;

import pProject.pPro.User.exception.UserErrorCode;

public class BookmarkException extends RuntimeException{
	 private final BookmarkErrorCode errorCode;
	    private final String customMessage;

	    // 에러 코드만 전달 (기본 메시지 사용)
	    public BookmarkException(BookmarkErrorCode errorCode) {
	        super(errorCode.name());
	        this.errorCode = errorCode;
	        this.customMessage = null;
	    }

	    // 에러 코드 + 사용자 정의 메시지 전달
	    public BookmarkException(BookmarkErrorCode errorCode, String customMessage) {
	        super(customMessage);
	        this.errorCode = errorCode;
	        this.customMessage = customMessage;
	    }
}
