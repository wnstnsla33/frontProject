package pProject.pPro.Report.exception;

public class ReportException extends RuntimeException{
	 private final ReportErrorCode errorCode;

	    public ReportException(String message, ReportErrorCode errorCode) {
	        super(message);
	        this.errorCode = errorCode;
	    }
	    public ReportException(ReportErrorCode errorCode) {
	        this.errorCode = errorCode;
	    }

	    public ReportErrorCode getErrorCode() {
	        return errorCode;
	    }
}
