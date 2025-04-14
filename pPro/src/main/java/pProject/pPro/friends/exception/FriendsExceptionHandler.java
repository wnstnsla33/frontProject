package pProject.pPro.friends.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import pProject.pPro.CommonResponse;

@RestControllerAdvice
@Slf4j
public class FriendsExceptionHandler {

    @ExceptionHandler(FriendsException.class)
    public ResponseEntity<CommonResponse<Void>> handleChatException(FriendsException e) {
        log.warn("FriendsException 발생: {} - {}", e.getErrorCode(), e.getMessage());

        String message = switch (e.getErrorCode()) {
            case NOT_FOUND_FRIENDS_ID -> "잘못된 요청입니다.";
            case UNKNOWN_ERROR -> "알 수 없는 오류가 발생했습니다.";
            case DUPLICATE_REQUEST ->"이미 친구로 되어있습니다.";
            case ALREADY_REPONSE ->"이미 처리된 요청입니다.";
            case TOO_MANY_REQUIRE ->"5회 이상의 요청은 불가합니다.";
        };

        HttpStatus status = switch (e.getErrorCode()) {
            case NOT_FOUND_FRIENDS_ID -> HttpStatus.NOT_FOUND;
            case UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case DUPLICATE_REQUEST,ALREADY_REPONSE,TOO_MANY_REQUIRE -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status)
                .body(CommonResponse.fail(message));
    }

}
