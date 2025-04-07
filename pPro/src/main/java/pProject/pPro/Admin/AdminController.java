package pProject.pPro.Admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.Admin.dto.SearchDTO;
import pProject.pPro.Admin.dto.UserChatByAdmin;
import pProject.pPro.CommonResponse;
import pProject.pPro.Admin.dto.AdminUserDTO;
import pProject.pPro.Admin.dto.UserDetailByAdmimDTO;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.post.DTO.PostListDTO;

@RestController
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminService;

	@GetMapping("/admin")
	public ResponseEntity<?> getLoginUserRole() {
		return ResponseEntity.ok(CommonResponse.success("관리자 권한 계정입니다"));
	}

	@GetMapping("/admin/user")
	public ResponseEntity<?> getUsersList(@ModelAttribute SearchDTO searchDTO) {
		List<AdminUserDTO> users = adminService.getUserList(searchDTO);
		return ResponseEntity.ok(CommonResponse.success("유저 리스트 조회 성공", users));
	}

	@GetMapping("/admin/user/{userId}")
	public ResponseEntity<?> getUserDetailInfo(@PathVariable("userId") Long userId) {
		UserDetailByAdmimDTO detail = adminService.getUserDetailInfo(userId);
		return ResponseEntity.ok(CommonResponse.success("유저 상세 정보 조회 성공", detail));
	}

	@DeleteMapping("/admin/user/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
		adminService.deleteUserById(userId);
		return ResponseEntity.ok(CommonResponse.success("유저 삭제 완료"));
	}

	@GetMapping("/admin/chat/{userId}")
	public ResponseEntity<?> getUserChatByAdmin(
			@PathVariable("userId") Long userId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "room", defaultValue = "") String roomKeyword) {
		List<UserChatByAdmin> chatList = adminService.getUserChatsByAdmin(userId, page, roomKeyword);
		return ResponseEntity.ok(CommonResponse.success("유저 채팅 조회 성공", chatList));
	}

	@GetMapping("/admin/room/{userId}")
	public ResponseEntity<?> getUserRooms(@PathVariable("userId") Long userId) {
		List<RoomDTO> rooms = adminService.getUserRoomsByAdmin(userId);
		return ResponseEntity.ok(CommonResponse.success("유저 참여 방 조회 성공", rooms));
	}

	@DeleteMapping("/admin/post/{postId}")
	public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {
		adminService.deletePostById(postId);
		return ResponseEntity.ok(CommonResponse.success("게시글이 강제 삭제되었습니다."));
	}

	@GetMapping("/admin/post")
	public ResponseEntity<?> postList(@ModelAttribute SearchDTO searchDTO) {
		List<PostListDTO> postList = adminService.getPostListByAdmin(searchDTO);
		return ResponseEntity.ok(CommonResponse.success("게시판 전체 목록 조회 성공", postList));
	}

	@GetMapping("/admin/room")
	public ResponseEntity<?> roomList(@ModelAttribute SearchDTO searchDTO) {
		List<RoomDTO> rooms = adminService.getRoomListByAdmin(searchDTO);
		return ResponseEntity.ok(CommonResponse.success("채팅방 목록 조회 성공", rooms));
	}

	@DeleteMapping("/admin/room/{roomId}")
	public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
		adminService.deleteRoomByAdmin(roomId);
		return ResponseEntity.ok(CommonResponse.success("방이 삭제되었습니다."));
	}
}