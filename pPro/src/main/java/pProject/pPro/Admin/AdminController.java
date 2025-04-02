package pProject.pPro.Admin;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.Admin.dto.AdminControllerResponseDTO;
import pProject.pPro.Admin.dto.AdminEnum;
import pProject.pPro.Admin.dto.AdminServiceResponseDTO;
import pProject.pPro.Admin.dto.SearchDTO;
import pProject.pPro.Admin.dto.UserChatByAdmin;
import pProject.pPro.User.UserEnum;
import pProject.pPro.User.UserService;
import pProject.pPro.User.DTO.ResponseUserDTO;
import pProject.pPro.User.DTO.UserServiceResponseDTO;
import pProject.pPro.post.PostService;
import pProject.pPro.post.DTO.PostServiceDTO;
import pProject.pPro.post.DTO.WritePostDTO;
import pProject.pPro.reply.ReplyService;
import pProject.pPro.room.RoomService;
import pProject.pPro.room.DTO.RoomServiceDTO;
import pProject.pPro.room.chat.ChatService;
@RequiredArgsConstructor
@RestController()
public class AdminController {
	private final AdminService adminService;
	
	@GetMapping("/admin")
	public ResponseEntity getLoginUserRole() {
		return new AdminControllerResponseDTO().AdminSuccess("관리자 권한 계정입니다");
	}
	//유저 리스트
	@GetMapping("/admin/user")
	public ResponseEntity getUsersList(@ModelAttribute SearchDTO searchDTO) {
		AdminServiceResponseDTO dto =  adminService.getUserList(searchDTO);
		return new AdminControllerResponseDTO().AdminSuccessType(dto);
	}
	@GetMapping("/admin/user/{userId}")
	public ResponseEntity<?> getUserDetailInfo(@PathVariable("userId") Long userId) {
	    AdminServiceResponseDTO<?> dto = adminService.getUserDetailInfo(userId);

	    if (dto.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccessType(dto);
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("유저 정보를 불러오는 중 오류 발생");
	    }
	}


	//유저 삭제
	@DeleteMapping("/admin/user/{userId}")
	public ResponseEntity deleteUser(@PathVariable("userId")Long userId) {
		AdminServiceResponseDTO dto= adminService.deleteUserById(userId);
		if(dto.getStatus()==AdminEnum.SUCCESS)return new AdminControllerResponseDTO().AdminSuccessType(dto.getData());
		else return new AdminControllerResponseDTO().AdminFail("삭제중 오류가발생하였습니다.");
	}
	//유저 수정?
	//유저 밴
	//유저 채팅
	@GetMapping("/admin/chat/{userId}")
	public ResponseEntity<?> getUserChatByAdmin(
	    @PathVariable("userId") Long userId,
	    @RequestParam(name = "page", defaultValue = "0") int page,
	    @RequestParam(name = "room", defaultValue = "") String roomKeyword
	) {
	    AdminServiceResponseDTO<List<UserChatByAdmin>> dto =
	        adminService.getUserChatsByAdmin(userId, page, roomKeyword);

	    if (dto.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccessType(dto);
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("채팅 조회 실패");
	    }
	}
	@GetMapping("/admin/room/{userId}")
	public ResponseEntity<?> getUserRooms(@PathVariable("userId") Long userId) {
	    AdminServiceResponseDTO<?> dto = adminService.getUserRoomsByAdmin(userId);
	    if (dto.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccessType(dto);
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("유저 참여 방 조회 실패");
	    }
	}

	
	//게시판 공지사항 등록 어차피 db에서 admin으로 빼오기떄문에 post에서 처리
	
	//게시판 강제 삭제
	@DeleteMapping("/admin/post/{postId}")
	public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {
	    AdminServiceResponseDTO result = adminService.deletePostById(postId);
	    if (result.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccess("게시글이 강제 삭제되었습니다.");
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("게시글 삭제 중 오류가 발생했습니다.");
	    }
	}
	//게시판 모든 목록(비밀방도 다)
	@GetMapping("/admin/post")
	public ResponseEntity postList(@ModelAttribute SearchDTO searchDTO) {
	    AdminServiceResponseDTO dto = adminService.getPostListByAdmin(searchDTO);
	    if (dto.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccessType(dto);
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("게시판을 불러오는 중 오류가 발생했습니다.");
	    }
	}

	//메세지도 만들어야되네
	
	//박스 
	
	//박스목록
	@GetMapping("/admin/room")
	public ResponseEntity<?> roomList(@ModelAttribute SearchDTO searchDTO) {
	    AdminServiceResponseDTO<?> dto = adminService.getRoomListByAdmin(searchDTO);
	    if (dto.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccessType(dto);
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("채팅방 목록을 불러오는 중 오류가 발생했습니다.");
	    }
	}

	//박스 삭제
	@DeleteMapping("/admin/room/{roomId}")
	public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
	    AdminServiceResponseDTO result = adminService.deleteRoomByAdmin(roomId);
	    if (result.getStatus() == AdminEnum.SUCCESS) {
	        return new AdminControllerResponseDTO().AdminSuccess("방이 삭제되었습니다.");
	    } else {
	        return new AdminControllerResponseDTO().AdminFail("방 삭제 중 오류가 발생했습니다.");
	    }
	}

	//채팅 목록
	
	//신고 목록
	
	//경고 메세지
	
}
