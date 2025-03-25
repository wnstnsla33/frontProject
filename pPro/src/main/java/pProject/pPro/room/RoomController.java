package pProject.pPro.room;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomResponseDTO;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	
	private final RoomResponseDTO roomResponseDTO;
	
	@PostMapping("/chatRoom")
	public ResponseEntity createRoom(RoomDTO dto, @AuthenticationPrincipal UserDetails user) {
		return RoomResponseDTO.roomInfo(roomService.createRoom(dto,user.getUsername()));
	}
	@GetMapping("/chatRoom/{roomId}")
	public ResponseEntity findRoom(@RequestParam String roomId) {
		return RoomResponseDTO.roomInfo(roomService.findRoom(roomId));
	}
	
}
