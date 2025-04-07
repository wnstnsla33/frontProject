package pProject.pPro.Report.exception;

public class ReportException extends RuntimeException{
	 private final String errorCode;

	    public ReportException(String message, String errorCode) {
	        super(message);
	        this.errorCode = errorCode;
	    }

	    public String getErrorCode() {
	        return errorCode;
	    }
}
