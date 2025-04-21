package pProject.pPro.smtp;

import pProject.pPro.friends.exception.FriendsErrorCode;
import pProject.pPro.global.CustomException;

public class SmtpException extends CustomException {
    public SmtpException(SmtpErrorCode errorCode) {
        super(errorCode);
    }
}
