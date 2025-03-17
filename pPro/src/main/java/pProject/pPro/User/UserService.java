package pProject.pPro.User;

import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pProject.pPro.User.DTO.ResponseUserDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;

@Service
public class UserService {
	ResponseUserDTO responseUserDTO;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public ResponseEntity saveUser(SignupLoginDTO signupDTO) {
		UserEntity isExist = userRepository.findByuserEmail(signupDTO.getEmail());
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
		userRepository.save(userEntity);
		return responseUserDTO.signupSuccess();
	}

	public ResponseEntity LoginUser(SignupLoginDTO dto) {
		UserEntity isExist = userRepository.findByuserEmail(dto.getEmail());
		if (isExist == null)
			return responseUserDTO.accountNotFound();
		else if (passwordEncoder.matches(dto.getPassword(), isExist.getUserPassword())) {
			return ResponseUserDTO.loginSuccess();
		}
		return ResponseUserDTO.loginFail();
	}

	public ResponseEntity<?> userInfo(String email) {
		UserEntity findEntity = userRepository.findByuserEmail(email);
		UserInfoDTO dto = new UserInfoDTO(findEntity);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
}
