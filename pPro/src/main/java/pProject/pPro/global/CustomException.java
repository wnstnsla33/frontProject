package pProject.pPro.global;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {
    private final BaseErrorCode errorCode;
    private final String customMessage;

    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // ✅ 커스텀 메시지를 받는 생성자 추가
    public CustomException(BaseErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }
}
