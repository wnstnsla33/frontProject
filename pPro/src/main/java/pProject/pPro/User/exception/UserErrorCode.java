package pProject.pPro.User.exception;

import pProject.pPro.global.BaseErrorCode;

public enum UserErrorCode implements BaseErrorCode {
    EXIST_ID("이미 존재하는 아이디입니다.", 409),
    NO_EXIST_ID("존재하지 않는 아이디입니다.", 404),
    INVALID_ID("존재하지 않는 아이디입니다.", 404),
    INVALID_EMAIL("잘못된 이메일 형식입니다.", 404),
    INVALID_PASSWORD("비밀번호가 잘못됐습니다.", 401),
    INVALID_BIRTH_DAY("생일 정보가 일치하지 않습니다.", 400),
    INVALID_NAME("이름이 일치하지 않습니다.", 400),
    ISSOCIAL("소셜 계정은 해당 기능을 사용할 수 없습니다.", 400),
    UNKNOWN("서버 오류입니다. 잠시 후 다시 시도해주세요.", 500),
    REQUIRED_LOGIN("로그인이 필요한 서비스입니다.", 403),
    EXIST_NICKNAME("중복 닉네임이 있습니다.", 409);

    private final String message;
    private final int status;

    UserErrorCode(String message, int status) {
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
