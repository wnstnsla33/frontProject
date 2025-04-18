package pProject.pPro.friends.exception;

import lombok.Getter;
import pProject.pPro.global.CustomException;

@Getter
public class FriendsException extends CustomException {
    public FriendsException(FriendsErrorCode errorCode) {
        super(errorCode);
    }
}
