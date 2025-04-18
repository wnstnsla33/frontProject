package pProject.pPro.Admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.Admin.dto.SearchDTO;
import pProject.pPro.Admin.dto.UserChatByAdmin;
import pProject.pPro.Admin.dto.AdminPagingDTO;
import pProject.pPro.Admin.dto.AdminUserDTO;
import pProject.pPro.Admin.dto.UserDetailByAdmimDTO;
import pProject.pPro.Report.ReportService;
import pProject.pPro.Report.ReportStatus;
import pProject.pPro.Report.DTO.ReportControllerDTO;
import pProject.pPro.Report.DTO.ReportPageDTO;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.DTO.ReportSearchDTO;
import pProject.pPro.Report.DTO.ReportStatusDTO;
import pProject.pPro.global.CommonResponse;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.post.DTO.PostListDTO;

@RestController
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminService;
	private final ReportService reportService;
	@GetMapping("/admin")
	public ResponseEntity<?> getLoginUserRole() {
		return ResponseEntity.ok(CommonResponse.success("관리자 권한 계정입니다"));
	}

	@GetMapping("/admin/user")
	public ResponseEntity<?> getUsersList(@ModelAttribute SearchDTO searchDTO) {
		AdminPagingDTO users = adminService.getUserList(searchDTO);
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
	public ResponseEntity<?> deleteRoom(@PathVariable("roomId") String roomId) {
		adminService.deleteRoomByAdmin(roomId);
		return ResponseEntity.ok(CommonResponse.success("방이 삭제되었습니다."));
	}
	 // ✅ 2. 신고 단일 조회
    @GetMapping("/admin/reports/{reportId}")
    public ResponseEntity<ReportControllerDTO<ReportResponseDTO>> findReport(@PathVariable("reportId") Long id) {
        ReportResponseDTO result = reportService.findReport(id);
        return ResponseEntity.ok(ReportControllerDTO.success("신고 조회 성공", result));
    }

    // ✅ 3. 신고 상태 변경 (관리자)
    @PutMapping("/admin/reports/{reportId}")
    public ResponseEntity<ReportControllerDTO<ReportStatus>> updateStatus(@PathVariable("reportId") Long reportId,
                                                                   @RequestBody ReportStatusDTO dto,@AuthenticationPrincipal UserDetails user) {
    	ReportStatus status =  reportService.updateStatus(dto, reportId,user.getUsername());
        return ResponseEntity.ok(ReportControllerDTO.success("신고 상태가 "+status+"로변경되었습니다.",status));
    }

    // ✅ 4. 관리자 신고 리스트 조회 (검색 + 페이징)
    @GetMapping("/admin/reports")
    public ResponseEntity<ReportControllerDTO<ReportPageDTO>> getReportList(@ModelAttribute ReportSearchDTO dto) {
        return ResponseEntity.ok(ReportControllerDTO.success("신고 리스트 조회 성공", reportService.getReportList(dto)));
    }
}