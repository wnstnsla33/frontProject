package pProject.pPro.smtp;

import pProject.pPro.global.BaseErrorCode;

public enum SmtpErrorCode implements BaseErrorCode{
	REQUIRED_SMTP("만료된 인증코드입니다.",400),
	UNVALID_SMTP("인증코드가 틀렸습니다.",400),
	SMTP_ERROR("메세지 송신 중 에러가 발생하였습니다.", 404);

    private final String message;
    private final int status;

    SmtpErrorCode(String message, int status) {
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
