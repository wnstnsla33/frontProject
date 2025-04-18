package pProject.pPro.User.exception;

import pProject.pPro.global.CustomException;

public class UserException extends CustomException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(UserErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
