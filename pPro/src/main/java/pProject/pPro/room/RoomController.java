package pProject.pPro.room;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomEnum;
import pProject.pPro.room.DTO.RoomResponseDTO;
import pProject.pPro.room.DTO.RoomServiceDTO;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private RoomResponseDTO roomResponseDTO;
	
	@PostMapping("/chatRoom")//채팅방 만들기
	public ResponseEntity createRoom(@RequestBody RoomDTO dto, @AuthenticationPrincipal UserDetails user) {
		return RoomResponseDTO.roomInfo(roomService.createRoom(dto,user.getUsername()));
	}
	@GetMapping("/chatRoom")//채팅방 목록 확인(아직 꾸미지않음)
	public ResponseEntity roomList() {
		return roomResponseDTO.roomSuccessType(roomService.roomList());
	}
	@GetMapping("/chatRoom/{roomId}")
	public ResponseEntity findRoom(@PathVariable("roomId") String roomId,@AuthenticationPrincipal UserDetails user) {
		System.out.println("방 리스트"+roomId);
		RoomServiceDTO dto = roomService.joinRoom(roomId, user.getUsername());
		if(dto.getState()==RoomEnum.ROOM_SUCCESS)return roomResponseDTO.roomSuccessType(dto.getData());
		else return roomResponseDTO.roomFailType(dto.getData());
	}
	@DeleteMapping("/chatRoom/{roomId}")
	public ResponseEntity deleteRoom(@PathVariable("roomId") String roomId,@AuthenticationPrincipal UserDetails user) {
		RoomServiceDTO<String> roomServiceDto =  roomService.deleteRoom(roomId, user.getUsername());
		if(roomServiceDto.getState()==RoomEnum.ROOM_SUCCESS) {
			
			return roomResponseDTO.roomMsgSuccess(roomServiceDto.getData());
		}
		else return roomResponseDTO.roomMsgFail(roomServiceDto.getData());
	}
	
}
