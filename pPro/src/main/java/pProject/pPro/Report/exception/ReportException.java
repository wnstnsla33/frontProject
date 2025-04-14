package pProject.pPro.Report.exception;

import pProject.pPro.global.CustomException;

public class ReportException extends CustomException {
    public ReportException(ReportErrorCode errorCode) {
        super(errorCode);
    }
}
