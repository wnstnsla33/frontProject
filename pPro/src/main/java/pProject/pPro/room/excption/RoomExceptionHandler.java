package pProject.pPro.room.excption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class RoomExceptionHandler {

    @ExceptionHandler(RoomException.class)
    public ResponseEntity<CommonResponse<Void>> handleRoomException(RoomException e) {
        log.warn("RoomException 발생: {}", e.getErrorCode());

        String message = (e.getCustomMessage() != null) ? e.getCustomMessage() : switch (e.getErrorCode()) {
            case ROOM_NOT_FOUND -> "해당 방이 존재하지 않습니다.";
            case USER_NOT_FOUND -> "사용자를 찾을 수 없습니다.";
            case ROOM_JOIN_FAIL -> "방 참가에 실패했습니다.";
            case FULL_CAPACITY -> "방의 정원이 꽉 찼습니다.";
            case INVALID_PASSWORD -> "비밀번호가 일치하지 않습니다.";
            case NO_PERMISSION -> "권한이 없습니다.";
            case NOT_JOINED -> "해당 방에 입장하지 않았습니다.";
            case INVALID_ID -> "해당 방이 존재하지 않습니다.";
            case UNKNOWN_ERROR -> "알 수 없는 오류가 발생했습니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case ROOM_NOT_FOUND,INVALID_ID -> HttpStatus.NOT_FOUND;
            case USER_NOT_FOUND, INVALID_PASSWORD, NOT_JOINED -> HttpStatus.UNAUTHORIZED;
            case FULL_CAPACITY, ROOM_JOIN_FAIL -> HttpStatus.BAD_REQUEST;
            case NO_PERMISSION -> HttpStatus.FORBIDDEN;
            case UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }

}
