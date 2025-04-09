package pProject.pPro.User.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final UserErrorCode errorCode;
    private final String customMessage;

    // 에러 코드만 전달 (기본 메시지 사용)
    public UserException(UserErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // 에러 코드 + 사용자 정의 메시지 전달
    public UserException(UserErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }
}
