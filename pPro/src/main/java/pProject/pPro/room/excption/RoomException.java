package pProject.pPro.room.excption;

import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.global.CustomException;

public class RoomException extends CustomException {
    public RoomException(RoomErrorCode errorCode) {
        super(errorCode);
    }
    public RoomException(RoomErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
