package pProject.pPro.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import pProject.pPro.User.DTO.*;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.CommonResponse;
import pProject.pPro.global.ControllerUtils;
import pProject.pPro.post.DTO.PassWordDTO;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ControllerUtils utils;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Void>> login(@RequestBody SignupLoginDTO signupDTO) {
        userService.loginUser(signupDTO);
        return ResponseEntity.ok(CommonResponse.success("로그인 성공"));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Void>> signup(@RequestBody @Valid SignupLoginDTO dto) {
    	
        userService.signup(dto);
        return ResponseEntity.ok(CommonResponse.success("회원가입 성공"));
    }

    // 내 정보 조회
    @GetMapping("/user")
    public ResponseEntity<CommonResponse<UserInfoDTO>> myInfo(@AuthenticationPrincipal UserDetails loginUser) {
    	if(loginUser==null)return ResponseEntity.ok(CommonResponse.success("미로그인 상태입니다."));
        UserEntity user = userService.findUserSync(utils.findEmail(loginUser));
        return ResponseEntity.ok(CommonResponse.success("내 정보 조회 성공", new UserInfoDTO(user)));
    }


    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok(CommonResponse.success("로그아웃 성공"));
    }

    // 프로필 수정
    @PostMapping("/user/edit")
    public ResponseEntity<CommonResponse<UserInfoDTO>> profileEdit(
            @ModelAttribute ProfileEditDTO profileEditDTO,
            @AuthenticationPrincipal UserDetails loginUser) {
        UserEntity userInfo = userService.updateUser(profileEditDTO, utils.findEmail(loginUser));
        return ResponseEntity.ok(CommonResponse.success("프로필 수정 성공", new UserInfoDTO(userInfo)));
    }

    // 회원 탈퇴
    @DeleteMapping("/user/delete")
    public ResponseEntity<CommonResponse<Void>> deleteUser(@AuthenticationPrincipal UserDetails loginUser,
                                                              @RequestBody PassWordDTO pwd) {
        userService.deleteUser(utils.findEmail(loginUser), pwd.getPwd());
        return ResponseEntity.ok(CommonResponse.success("회원 탈퇴 완료"));
    }

    // 아이디 찾기
    @PostMapping("/find/id")
    public ResponseEntity<CommonResponse<String>> findId(@RequestBody UserInfoDTO userInfoDTO) {
        String email = userService.findId(userInfoDTO);
        return ResponseEntity.ok(CommonResponse.success("아이디 찾기 성공", email));
    }

    // 비밀번호 찾기
    @PostMapping("/find/pwd")
    public ResponseEntity<CommonResponse<String>> findPwd(@RequestBody UserInfoDTO userInfoDTO) {
        String result = userService.findPwd(userInfoDTO);
        return ResponseEntity.ok(CommonResponse.success("비밀번호 찾기 성공", result));
    }
    @GetMapping("/user/detail/{userId}")
    public ResponseEntity detailUserInfo(@PathVariable("userId")Long userId) {
    	UserDetailDTO user = userService.getUserInfo(userId);
    	return ResponseEntity.ok(CommonResponse.success("해당 유저의 정보입니다.", user));
    }
}
