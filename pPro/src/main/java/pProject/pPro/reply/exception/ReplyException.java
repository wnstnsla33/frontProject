package pProject.pPro.reply.exception;

import lombok.Getter;
import pProject.pPro.post.exception.PostErrorCode;
@Getter
public class ReplyException extends RuntimeException{
	private final ReplyErrorCode errorCode;
    private final String customMessage;

    // 에러 코드만 전달 (기본 메시지 사용)
    public ReplyException(ReplyErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // 에러 코드 + 사용자 정의 메시지 전달
    public ReplyException(ReplyErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }}
