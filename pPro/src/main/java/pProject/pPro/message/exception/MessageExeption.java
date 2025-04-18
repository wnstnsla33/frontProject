package pProject.pPro.message.exception;

import pProject.pPro.global.CustomException;

public class MessageExeption extends CustomException {
    public MessageExeption(MessageErrorCode errorCode) {
        super(errorCode);
    }
}
