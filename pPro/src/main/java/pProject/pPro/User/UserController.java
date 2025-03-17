package pProject.pPro.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.entity.UserEntity;

@RestController

public class UserController {
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
	public ResponseEntity<?> signUpUser(@RequestBody SignupLoginDTO signupDTO){
		System.out.println("**************************signup.post");
		System.out.println(signupDTO.getEmail());
		return userService.saveUser(signupDTO);
	}
	@GetMapping("myInfo")
	public ResponseEntity<?> myInfo(@AuthenticationPrincipal UserDetails loginUser){
		System.out.println("**********************myinfo"+loginUser.getUsername());
		return userService.userInfo(loginUser.getUsername());
	}
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
	    Cookie cookie = new Cookie("Authorization", null);
	    cookie.setPath("/");
	    cookie.setHttpOnly(true);
	    cookie.setMaxAge(0);  
	    cookie.setSecure(true);  

	    response.addCookie(cookie);
	    return ResponseEntity.ok().build();
	}
}
