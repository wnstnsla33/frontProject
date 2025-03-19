package pProject.pPro.bookmark.DTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BookmarkResponseDTO {
	private int state;
	private String messsage;
	
	public static ResponseEntity regBookmarkSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("북마크 등록완료!");
    }
	
	public static ResponseEntity bookmarkSuccessMsg(String msg) {
    	return ResponseEntity.status(HttpStatus.OK).body(msg);
    }
	public static ResponseEntity bookmarkFailMsg(String msg) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
	public static<T> ResponseEntity bookmarkTypeSuccees(T t) {
    	return ResponseEntity.status(HttpStatus.OK).body(t);
    }
}
