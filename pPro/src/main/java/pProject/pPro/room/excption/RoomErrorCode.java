package pProject.pPro.room.excption;

import pProject.pPro.global.BaseErrorCode;

public enum RoomErrorCode implements BaseErrorCode {
    ROOM_NOT_FOUND("해당 채팅방이 존재하지 않습니다.", 404),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", 401),
    ROOM_JOIN_FAIL("채팅방 참가에 실패했습니다.", 400),
    FULL_CAPACITY("채팅방의 정원이 꽉 찼습니다.", 400),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", 401),
    NO_PERMISSION("권한이 없습니다.", 403),
    NOT_JOINED("해당 채팅에 입장하지 않았습니다.", 401),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.", 500),
    INVALID_ID("해당 채팅방이 존재하지 않습니다.", 404),
    ISBANNED("방입장이 금지되었습니다.",403),
    IS_ONLY_HOST("방장만이 밴할수 있습니다.",403),
    DUPLICATE_ROOM("채팅방을 중복 생성할 수 없습니다.", 400);

    private final String message;
    private final int status;

    RoomErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
