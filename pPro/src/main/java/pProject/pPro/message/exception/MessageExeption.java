package pProject.pPro.message.exception;

import lombok.Getter;
import pProject.pPro.reply.exception.ReplyErrorCode;
@Getter
public class MessageExeption extends RuntimeException{
	private final MessageErrorCode errorCode;
    private final String customMessage;

    // 에러 코드만 전달 (기본 메시지 사용)
    public MessageExeption(MessageErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // 에러 코드 + 사용자 정의 메시지 전달
    public MessageExeption(MessageErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }
}
