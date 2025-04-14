package pProject.pPro.friends;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pProject.pPro.CommonResponse;
import pProject.pPro.ControllerUtils;
import pProject.pPro.Admin.AdminService;
import pProject.pPro.Report.ReportService;
import pProject.pPro.friends.DTO.FriendsListDTO;
import pProject.pPro.friends.DTO.FriendsRequestDTO;
import pProject.pPro.friends.DTO.RequestFriendsType;

@RestController
@RequiredArgsConstructor
public class FriendsController {
	private final ControllerUtils utils;
	private final FriendsService friendsService;
	
	@PostMapping("/friends/{friendsUserId}")
	public ResponseEntity requestFriends(@PathVariable("friendsUserId")Long fId, @AuthenticationPrincipal UserDetails user) {
		String email = utils.findEmail(user);
		friendsService.requestFriends(email, fId);
		return ResponseEntity.ok(CommonResponse.success("친구 추가 요청했습니다"));
	}
	
	@PutMapping("/friends/{requestFId}")
	public ResponseEntity updateF(@PathVariable("requestFId")Long requestfId,
			@RequestBody RequestFriendsType type,@AuthenticationPrincipal UserDetails user) {
		String email = utils.findEmail(user);
		friendsService.updateF(email, requestfId, type);
		if(type==RequestFriendsType.ACCEPT)return ResponseEntity.ok(CommonResponse.success("수락하였습니다."));
		else return ResponseEntity.ok(CommonResponse.success("거절하였습니다."));
	}
	
	@GetMapping("/friends/request/count")
	public ResponseEntity requestCount(@AuthenticationPrincipal UserDetails user) {
		int requestCount = friendsService.getRequestCount(utils.findEmail(user));
		return ResponseEntity.ok(CommonResponse.success("친구 요청 수",requestCount));
	}
	@GetMapping("/friends/request")
	public ResponseEntity requestList(@AuthenticationPrincipal UserDetails user,@RequestParam(name ="page", defaultValue = "0") int page) {
		    Pageable pageable = PageRequest.of(page, 10, Sort.by("friendsId").descending());
		    Page<FriendsRequestDTO> result = friendsService.findRequestList(user.getUsername(), pageable);
		    return ResponseEntity.ok(CommonResponse.success("친구 요청 목록", result));
		}
	
	@GetMapping("/friends")
	public ResponseEntity friendsList(@AuthenticationPrincipal UserDetails user) {
		    List<FriendsListDTO> result = friendsService.findFriendsList(utils.findEmail(user));
		    return ResponseEntity.ok(CommonResponse.success("친구 목록", result));
		}
}
