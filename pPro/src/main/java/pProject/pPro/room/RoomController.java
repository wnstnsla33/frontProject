package pProject.pPro.room;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import pProject.pPro.CommonResponse;
import pProject.pPro.ControllerUtils;
import pProject.pPro.chat.DTO.ChatMessageDTO;
import pProject.pPro.chat.DTO.MessageResponseDTO;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.SearchRoomDTO;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private final ControllerUtils utils;
	@PostMapping("/chatRoom")
	public ResponseEntity<?> createRoom(@ModelAttribute RoomDTO dto,
	                                    @AuthenticationPrincipal UserDetails user) {
		RoomDTO createdRoom = roomService.createRoom(dto, utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("방이 생성되었습니다", createdRoom));
	}

//	@GetMapping("/chatRoom")
//	public ResponseEntity<?> getRoomList() {
//		List<RoomDTO> rooms = roomService.roomList();
//		return ResponseEntity.ok(CommonResponse.success("전체 방 목록 조회 성공", rooms));
//	}

	@GetMapping("/chatRoom/{roomId}")
	public ResponseEntity<?> findRoom(@PathVariable("roomId") String roomId,
	                                  @AuthenticationPrincipal UserDetails user) {
		RoomDTO joinedRoom = roomService.joinRoom(roomId, utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("방 참가 완료", joinedRoom));
	}

	@PostMapping("/chatRoom/{roomId}/verify")
	public ResponseEntity<?> joinWithPassword(@PathVariable("roomId") String roomId,
	                                           @RequestParam("password") String password,
	                                           @AuthenticationPrincipal UserDetails user) {
		RoomDTO joinedRoom = roomService.joinPwdRoom(roomId, utils.findEmail(user), password);
		return ResponseEntity.ok(CommonResponse.success("비밀방 참가 완료", joinedRoom));
	}

	@DeleteMapping("/chatRoom/{roomId}")
	public ResponseEntity<?> deleteRoom(@PathVariable("roomId") String roomId,
	                                    @AuthenticationPrincipal UserDetails user) {
		roomService.deleteRoom(roomId,utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("방 삭제 또는 퇴장 처리 완료", null));
	}

	@PutMapping("/chatRoom/{roomId}")
	public ResponseEntity<?> updateRoom(@PathVariable("roomId") String roomId,
	                                    @RequestBody RoomDTO dto,
	                                    @AuthenticationPrincipal UserDetails user) {
		RoomDTO updatedRoom = roomService.updateRoom(dto, roomId, utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("방 정보 수정 완료", updatedRoom));
	}

	@GetMapping("/chatRoom/hostRooms")
	public ResponseEntity<?> getMyJoinRooms(@AuthenticationPrincipal UserDetails user) {
		List<RoomDTO> list = roomService.getMyJoinRooms(utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("참여 중인 방 목록 조회 성공", list));
	}

	@GetMapping("/chatRoom/{roomId}/messages")
	public ResponseEntity<?> getChatList(@PathVariable("roomId") String roomId,
	                                     @AuthenticationPrincipal UserDetails user) {
		List<MessageResponseDTO> chatList = roomService.getChatList(roomId, utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("채팅 내역 조회 성공", chatList));
	}

	@PostMapping("/chatRoom/image")
	public ResponseEntity<?> uploadRoomImage(@RequestPart("image") MultipartFile imageFile) {
		String savedUrl = roomService.savedImage(imageFile);
		return ResponseEntity.ok(CommonResponse.success("이미지 저장 성공", savedUrl));
	}

	@GetMapping("/chatRoom/search")
	public ResponseEntity<?> searchRooms(@ModelAttribute SearchRoomDTO dto) {
		var pageResult = roomService.searchRooms(dto);
		return ResponseEntity.ok(CommonResponse.success("방 검색 결과", pageResult));
	}
}
