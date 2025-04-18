package pProject.pPro.reply.exception;

import pProject.pPro.global.CustomException;
import pProject.pPro.post.exception.PostErrorCode;

public class ReplyException extends CustomException {
    public ReplyException(ReplyErrorCode errorCode) {
        super(errorCode);
    }
    public ReplyException(ReplyErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
