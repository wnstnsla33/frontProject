package pProject.pPro.post.exception;

import pProject.pPro.global.CustomException;
import pProject.pPro.room.excption.RoomErrorCode;

public class PostException extends CustomException {
    public PostException(PostErrorCode errorCode) {
        super(errorCode);
    }
    public PostException(PostErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
