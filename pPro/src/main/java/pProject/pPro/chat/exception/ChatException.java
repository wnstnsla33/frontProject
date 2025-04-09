package pProject.pPro.chat.exception;

public class ChatException extends RuntimeException{
	 private final ChatErrorCode errorCode;

	    public ChatException(String message, ChatErrorCode errorCode) {
	        super(message);
	        this.errorCode = errorCode;
	    }
	    public ChatException(ChatErrorCode errorCode) {
	        this.errorCode = errorCode;
	    }
	    public ChatErrorCode getErrorCode() {
	        return errorCode;
	    }
}
