package pProject.pPro.Admin.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pProject.pPro.room.DTO.RoomDTO;

public class AdminControllerResponseDTO {
	public static ResponseEntity AdminSuccess(String msg) {
		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}
	public static ResponseEntity AdminFail(String msg) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
	}
	public static<T> ResponseEntity AdminSuccessType(T t) {
		return ResponseEntity.status(HttpStatus.OK).body(t);
	}
	public static<T> ResponseEntity AdminFailType(T t) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(t);
	}
}
