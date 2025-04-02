package pProject.pPro.room.DTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

public class RoomResponseDTO {
	
	public static ResponseEntity roomMsgSuccess(String msg) {
		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}
	public static ResponseEntity roomMsgFail(String msg) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
	}
	public static ResponseEntity roomInfo(RoomDTO dto) {
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
	public static<T> ResponseEntity roomSuccessType(T t) {
		return ResponseEntity.status(HttpStatus.OK).body(t);
	}
	public static<T> ResponseEntity roomFailType(T t) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(t);
	}
}
	