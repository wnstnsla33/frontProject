package pProject.pPro.securityConfig.exception;

public class PostNotFoundException extends RuntimeException	{
	 public PostNotFoundException() {
	        super("찾는 게시물이 없습니다.");
	    }
	    public PostNotFoundException(String message) {
	        super(message);
	    }
}
