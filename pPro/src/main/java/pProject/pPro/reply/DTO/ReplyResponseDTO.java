package pProject.pPro.reply.DTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ReplyResponseDTO {
	private int status; // 상태 코드
    private String message; // 응답 메시지
    // 로그인
    public static ResponseEntity replySuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("댓글 등록되었습니다.");
    }
    public static ResponseEntity replyFail() {
    	return ResponseEntity.status(HttpStatus.OK).body("댓글 실패되었습니다.");
    }
    public static ResponseEntity replyList(List<ReplyListDTO> list) {
    	return ResponseEntity.status(HttpStatus.OK).body(list);
    }
    public static ResponseEntity replyList(ReplyListDTO dto) {
    	return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
    public static ResponseEntity replySuccess(String msg) {
    	return ResponseEntity.status(HttpStatus.OK).body("댓글 삭제되었습니다.");
    }
    public static ResponseEntity replyFail(String msg) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 삭제가 실패되었습니다.");
    }
}
