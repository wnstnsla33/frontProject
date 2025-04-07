package pProject.pPro.room.excption;

public class RoomException extends RuntimeException {

    private final RoomErrorCode errorCode;
    private final String customMessage;

    public RoomException(RoomErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    public RoomException(RoomErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public RoomErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
