package pProject.pPro.room;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pProject.pPro.room.DTO.PasswordDTO;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomEnum;
import pProject.pPro.room.DTO.RoomResponseDTO;
import pProject.pPro.room.DTO.RoomServiceDTO;
import pProject.pPro.room.DTO.SearchRoomDTO;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private RoomResponseDTO roomResponseDTO;
	
	@PostMapping("/chatRoom")//채팅방 만들기
	public ResponseEntity createRoom(@ModelAttribute RoomDTO dto,
			    @AuthenticationPrincipal UserDetails user) {
		return RoomResponseDTO.roomInfo(roomService.createRoom(dto,user.getUsername()));
	}
	@PostMapping("/chatRoom/image")
	public ResponseEntity savedImage(@RequestPart("image")MultipartFile image ) {
		if(image.isEmpty())return roomResponseDTO.roomMsgFail("이미지가 없습니다");
		else {
			return roomResponseDTO.roomMsgSuccess(roomService.savedImage(image));
		}
	}
	@GetMapping("/chatRoom")//채팅방 목록 확인(아직 꾸미지않음)
	public ResponseEntity roomList() {
		return roomResponseDTO.roomSuccessType(roomService.roomList());
	}
	@GetMapping("/chatRoom/search")
	public ResponseEntity searchRooms(
	    @ModelAttribute SearchRoomDTO searchRoomDTO ,@AuthenticationPrincipal UserDetails user) {
		searchRoomDTO.setEmail(user.getUsername());
	    return roomResponseDTO.roomSuccessType(roomService.searchRooms(searchRoomDTO));
	}

	@GetMapping("/chatRoom/{roomId}")
	public ResponseEntity findRoom(@PathVariable("roomId") String roomId,@AuthenticationPrincipal UserDetails user) {
		System.out.println(user.getUsername());
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
	@GetMapping("/chatRoom/{roomId}/messages")
	public ResponseEntity getMessages(@PathVariable("roomId") String roomId, @AuthenticationPrincipal UserDetails user) {
		System.out.println(roomId+"방 이름");
	    RoomServiceDTO<?> dto = roomService.getChatList(roomId, user.getUsername());
	    if (dto.getState() == RoomEnum.ROOM_SUCCESS) {
	        return RoomResponseDTO.roomSuccessType(dto.getData());
	    } else {
	        return RoomResponseDTO.roomFailType(dto.getData());
	    }
	}
	@GetMapping("/chatRoom/hostRooms")
	public ResponseEntity gethostRooms(@AuthenticationPrincipal UserDetails user) {
		RoomServiceDTO dto = roomService.GetMyJoinRooms(user.getUsername());
		return RoomResponseDTO.roomSuccessType(dto.getData());
	}
	@PostMapping("/chatRoom/{roomId}/verify")
	public ResponseEntity verifyPWD(@PathVariable("roomId")String roomId,@RequestBody PasswordDTO passwordDto,@AuthenticationPrincipal UserDetails user) {
		System.out.println(roomId+passwordDto.getPassword()+"********************");
		RoomServiceDTO dto = roomService.joinPwdRoom(roomId, user.getUsername(),passwordDto.getPassword());
		if(dto.getState()==RoomEnum.ROOM_SUCCESS)return roomResponseDTO.roomSuccessType(dto.getData());
		else return roomResponseDTO.roomFailType(dto.getData());
	}

}
