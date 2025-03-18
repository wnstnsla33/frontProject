package pProject.pPro.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import pProject.pPro.User.DTO.ProfileEditDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.entity.UserEntity;

@RestController

public class UserController {
	void makeMessage(String methodName) {
		System.out.println("********************************"+methodName);
	}
	@Autowired
	private UserService userService;
	
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
	public ResponseEntity signUpUser(@RequestBody SignupLoginDTO signupDTO){
		makeMessage("signiup");
		System.out.println(signupDTO.getEmail());
		return userService.saveUser(signupDTO);
	}
	@GetMapping("user")
	public ResponseEntity myInfo(@AuthenticationPrincipal UserDetails loginUser){
		makeMessage("profile");
		return userService.userInfo(loginUser.getUsername());
	}
	@PostMapping("user/logout")
	public ResponseEntity logout(HttpServletResponse response) {
		makeMessage("logout");
	    return userService.logout(response);
	}
	@PostMapping("/user/edit")
	public ResponseEntity profileEdit(@RequestBody ProfileEditDTO profileEditDTO, 
	                                     @AuthenticationPrincipal  UserDetails loginUser) {
		makeMessage("profileEdit");
	    return userService.updateUser(profileEditDTO, loginUser.getUsername());
	}

	@DeleteMapping("/user/{email}")
	public ResponseEntity deleteUser(@RequestParam("email")String email) {
		makeMessage("delete User");
		return userService.deleteUser(email);
	}
	
	@PostMapping("/find/id")
	public ResponseEntity findId(@RequestBody UserInfoDTO userInfoDTO) {
		makeMessage("find/id"+userInfoDTO);
		return userService.findId(userInfoDTO);
	}
	@PostMapping("/find/pwd")
	public ResponseEntity findPwd(@RequestBody UserInfoDTO userInfoDTO) {
		makeMessage("find/pwd");
		return userService.findPwd(userInfoDTO);
	}
}
