package pProject.pPro.securityConfig.exception;

import lombok.Getter;

@Getter
public class FilterException extends RuntimeException{
	  private final FilterErrorCode errorCode;
	    private final String customMessage;

	    // 에러 코드만 전달 (기본 메시지 사용)
	    public FilterException(FilterErrorCode errorCode) {
	        super(errorCode.name());
	        this.errorCode = errorCode;
	        this.customMessage = null;
	    }

	    // 에러 코드 + 사용자 정의 메시지 전달
	    public FilterException(FilterErrorCode errorCode, String customMessage) {
	        super(customMessage);
	        this.errorCode = errorCode;
	        this.customMessage = customMessage;
	    }
}
