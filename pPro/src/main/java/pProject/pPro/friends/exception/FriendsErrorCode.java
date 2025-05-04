package pProject.pPro.friends.exception;

import pProject.pPro.global.BaseErrorCode;

public enum FriendsErrorCode implements BaseErrorCode {
    NOT_FOUND_FRIENDS_ID("잘못된 요청입니다.", 404),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.", 500),
    DUPLICATE_REQUEST("이미 친구로 되어있습니다.", 400),
    ALREADY_REPONSE("이미 처리된 요청입니다.", 400),
    TOMANY_FRIENDS("친구수는 30명이 최대입니다.",400),
    ALREADY_REQUEST("친구 신청된 상태입니다.", 400),
;
    private final String message;
    private final int status;

    FriendsErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
