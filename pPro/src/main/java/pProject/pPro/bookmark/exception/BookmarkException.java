package pProject.pPro.bookmark.exception;

import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.global.CustomException;

public class BookmarkException extends CustomException {
    public BookmarkException(BookmarkErrorCode errorCode) {
        super(errorCode);
    }
}
