package pProject.pPro.chat.exception;

import pProject.pPro.global.CustomException;

public class ChatException extends CustomException {
    public ChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}
