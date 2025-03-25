package pProject.pPro.room.DTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RoomResponseDTO {
	private int status;  
	private String message; 


	public static ResponseEntity roomMsgSuccess(String msg) {
		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}
	public static ResponseEntity roomMsgFail(String msg) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
	}
	public static ResponseEntity roomInfo(RoomDTO dto) {
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
}
