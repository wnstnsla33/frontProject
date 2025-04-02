package pProject.pPro.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import pProject.pPro.User.DTO.ProfileEditDTO;
import pProject.pPro.User.DTO.ResponseUserDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.User.DTO.UserServiceResponseDTO;
import pProject.pPro.entity.UserEntity;

@RestController

public class UserController {
	void makeMessage(String methodName) {
		System.out.println("********************************"+methodName);
	}
	@Autowired
	private UserService userService;
	ResponseUserDTO responseUserDTO;
//	@PostMapping("/login")
//	public String main(@RequestBody SignupLoginDTO signupDTO) {
//		System.out.println(signupDTO.getEmail());
//		return "main";
//	}
	
	@GetMapping("/auth")
	public String auth() {
		return "auth";
	}
	@PostMapping("/signup")
	public ResponseEntity signUpUser(@RequestBody @Valid SignupLoginDTO signupDTO){
		makeMessage("signiup");
		String isSaved =  userService.saveUser(signupDTO);
		if(isSaved.equals("existId"))return responseUserDTO.existId();
		else if(isSaved.equals("success")) return responseUserDTO.signupSuccess();
		else return responseUserDTO.failInMsg("회원 가입에 실패하였습니다");
	}
	@GetMapping("user")
	public ResponseEntity myInfo(@AuthenticationPrincipal UserDetails loginUser){
		makeMessage("profile");
		return ResponseEntity.status(HttpStatus.OK).body(userService.userInfo(loginUser.getUsername()));
	}
	@PostMapping("/auth/logout")
	public ResponseEntity logout(HttpServletResponse response) {
		makeMessage("logout");
		userService.logout(response);
	    return responseUserDTO.logoutSuccess();
	}
	@PostMapping("/user/edit")
	public ResponseEntity profileEdit(@ModelAttribute ProfileEditDTO profileEditDTO,
	                                     @AuthenticationPrincipal  UserDetails loginUser) {
		makeMessage("profileEdit");
		UserEntity userInfo = userService.updateUser(profileEditDTO, loginUser.getUsername());
		if(userInfo==null) return responseUserDTO.loginFail();
		else return ResponseUserDTO.userInfo(userInfo);
	}

//	@DeleteMapping("/user/delete")//이거 프론트에 수정해야돼
//	public ResponseEntity deleteUser(@AuthenticationPrincipal  UserDetails loginUser) {
//		makeMessage("delete User");
//		UserServiceResponseDTO dto = userService.deleteUser(loginUser.getUsername());
//		if(dto.getStatus()==UserEnum.SUCCESS) return responseUserDTO.userDelete();
//		else return responseUserDTO.failInMsg("계정이 삭제되지 않았습니다");
//	}
	
	@PostMapping("/find/id")
	public ResponseEntity findId(@RequestBody UserInfoDTO userInfoDTO) {
	    makeMessage("find/id " + userInfoDTO);
	    UserServiceResponseDTO<String> response = userService.findId(userInfoDTO);
	    if(response.getStatus()==UserEnum.SUCCESS)return responseUserDTO.okInMsg(response.getData());
	    else return responseUserDTO.failInMsg(response.getData());
	}

	@PostMapping("/find/pwd")
	public ResponseEntity findPwd(@RequestBody UserInfoDTO userInfoDTO) {
	    makeMessage("find/pwd " + userInfoDTO);
	    UserServiceResponseDTO<String> response = userService.findPwd(userInfoDTO);
	    if(response.getStatus()==UserEnum.SUCCESS)return responseUserDTO.okInMsg(response.getData());
	    else return responseUserDTO.failInMsg(response.getData());
	}
	
}
