package pProject.pPro.User;

import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import pProject.pPro.User.DTO.ProfileEditDTO;
import pProject.pPro.User.DTO.ResponseUserDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;

@Service
@Transactional
public class UserService {
	ResponseUserDTO responseUserDTO;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public String createKey() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		String key = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		return key;
	}

	public ResponseEntity saveUser(SignupLoginDTO signupDTO) {
		UserEntity isExist = userRepository.findByEmail(signupDTO.getEmail());
		if (isExist != null)
			return responseUserDTO.existId();
		UserEntity userEntity = new UserEntity();
		userEntity.setUserEmail(signupDTO.getEmail());
		userEntity.setUserNickName(signupDTO.getNickname());
		userEntity.setUserName(signupDTO.getRealName());
		userEntity.setUserBirthDay(signupDTO.getBirthDate().toString());
		LocalDate birthYearStr = signupDTO.getBirthDate();
		LocalDate currentDate = LocalDate.now();
		int age = Period.between(birthYearStr, currentDate).getYears();
		userEntity.setUserAge(age);
		userEntity.setUserSex(signupDTO.getGender());
		userEntity.setUserGrade(Grade.BRONZE);
		userEntity.setUserCreateDate(LocalDate.now().toString());
		userEntity.setUserPassword(passwordEncoder.encode(signupDTO.getPassword()));
		userEntity.setUserImg(
				"https://i.namu.wiki/i/Bge3xnYd4kRe_IKbm2uqxlhQJij2SngwNssjpjaOyOqoRhQlNwLrR2ZiK-JWJ2b99RGcSxDaZ2UCI7fiv4IDDQ.webp");
		userEntity.setUserLevel(1);
		userEntity.setUserExp(0);
		userRepository.save(userEntity);
		return responseUserDTO.signupSuccess();
	}

	public ResponseEntity LoginUser(SignupLoginDTO dto) {
		UserEntity isExist = userRepository.findByEmail(dto.getEmail());
		if (isExist == null)
			return responseUserDTO.accountNotFound();
		else if (passwordEncoder.matches(dto.getPassword(), isExist.getUserPassword())) {
			return ResponseUserDTO.loginSuccess();
		}
		return ResponseUserDTO.loginFail();
	}

	public ResponseEntity<?> userInfo(String email) {
		UserEntity findEntity = userRepository.findByEmail(email);
		UserInfoDTO dto = new UserInfoDTO(findEntity);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	public ResponseEntity<?> updateUser(ProfileEditDTO profileEditDTO, String email) {
		UserEntity user = userRepository.findByEmail(email);
		if (!passwordEncoder.matches(user.getUserPassword(), profileEditDTO.getUserPassword())) {
			return responseUserDTO.loginFail();
		}
		if (profileEditDTO.getUserNewPassword() != null) {
			user.setUserNickName(passwordEncoder.encode(profileEditDTO.getUserNewPassword()));
		}
		if (profileEditDTO.getNickName() != null) {
			user.setUserNickName(profileEditDTO.getNickName());
		}
		// 이미지가 null이 아닐 때만 수정
		if (profileEditDTO.getUserImg() != null) {
			user.setUserImg(profileEditDTO.getUserImg());
		}
		// 정보가 null이 아닐 때만 수정
		if (profileEditDTO.getUserInfo() != null) {
			user.setUserInfo(profileEditDTO.getUserInfo());
		}

		return ResponseUserDTO.userInfo(userRepository.save(user));
	}

	public ResponseEntity expUp(String email) {// 게시판 등록 시 경험치 20
		UserEntity user = userRepository.findByEmail(email);
		int exp = user.getUserExp() + 20;
		if (exp == 100) {
			user.setUserLevel(user.getUserLevel() + 1);
			user.setUserExp(0);
			if (user.getUserLevel() >= 4) {
				user.setUserGrade(Grade.SILVER);
			} else if (user.getUserLevel() >= 7) {
				user.setUserGrade(Grade.GOLD);
			} else if (user.getUserLevel() >= 10) {
				user.setUserGrade(Grade.VIP);
			}
		} else
			user.setUserExp(exp);
		return ResponseUserDTO.userInfo(userRepository.save(user));
	}

	public ResponseEntity deleteUser(String email) {
		UserEntity user = userRepository.findByEmail(email);
		userRepository.deleteByUserEmail(email);
		return responseUserDTO.userDelete();
	}

	public ResponseEntity logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("Authorization", null);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookie.setSecure(true);
		response.addCookie(cookie);
		return responseUserDTO.logoutSuccess();
	}

	public ResponseEntity findId(UserInfoDTO userInfoDTO) {
		UserEntity user = userRepository.findByName(userInfoDTO.getUserName());
		if(user==null)return responseUserDTO.accountNotFound();
		else if(!user.getUserBirthDay().equals(userInfoDTO.getUserBirthDay())) {
			return responseUserDTO.failInMsg("생일이 잘못되었습니다");
		}
		else if(user.getUserEmail().startsWith("naver ")
				||user.getUserEmail().startsWith("google ")
				||user.getUserEmail().startsWith("kakao ")){
			return responseUserDTO.failInMsg("SNS로그인 계정은 id찾기 불가합니다.");
		}
		else return responseUserDTO.okInMsg("email 은"+user.getUserEmail());
	}

	public ResponseEntity findPwd(UserInfoDTO userInfoDTO) {
		if(userInfoDTO.getUserEmail().startsWith("naver ")
				||userInfoDTO.getUserEmail().startsWith("google ")
				||userInfoDTO.getUserEmail().startsWith("kakao ")) {
			return responseUserDTO.failInMsg("SNS로그인 계정은 pwd찾기 불가합니다.");
		}
		UserEntity user = userRepository.findByEmail(userInfoDTO.getUserEmail());
		
		if (user == null)
			return responseUserDTO.accountNotFound();
		else if (!user.getUserName().equals(userInfoDTO.getUserName())) {
			return responseUserDTO.failInMsg("이름이 잘못되었습니다");
		}

		else {
			String pwd = createKey();
			user.setUserPassword(passwordEncoder.encode(pwd));
			userRepository.save(user);
			return responseUserDTO.okInMsg(pwd + "로 재설정되었습니다.");
		}
	}
}
