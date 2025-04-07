package pProject.pPro.post.exception;

import lombok.Getter;
import pProject.pPro.User.Exception.UserErrorCode;

@Getter

public class PostException extends RuntimeException{
	  private final PostErrorCode errorCode;
	    private final String customMessage;

	    // 에러 코드만 전달 (기본 메시지 사용)
	    public PostException(PostErrorCode errorCode) {
	        super(errorCode.name());
	        this.errorCode = errorCode;
	        this.customMessage = null;
	    }

	    // 에러 코드 + 사용자 정의 메시지 전달
	    public PostException(PostErrorCode errorCode, String customMessage) {
	        super(customMessage);
	        this.errorCode = errorCode;
	        this.customMessage = customMessage;
	    }
}
