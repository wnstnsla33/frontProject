package pProject.pPro.friends.exception;

import lombok.Getter;

@Getter
public class FriendsException extends RuntimeException{
	 private final FriendsErrorCode errorCode;

	    public FriendsException(String message, FriendsErrorCode errorCode) {
	        super(message);
	        this.errorCode = errorCode;
	    }
	    public FriendsException(FriendsErrorCode errorCode) {
	        this.errorCode = errorCode;
	    }
	    public FriendsErrorCode getErrorCode() {
	        return errorCode;
	    }
}
