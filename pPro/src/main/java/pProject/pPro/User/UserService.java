package pProject.pPro.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.ServiceUtils;
import pProject.pPro.User.DTO.ProfileEditDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.entity.Address;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ServiceUtils utils;

    public String createKey() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void signup(SignupLoginDTO signupDTO) {
        Optional<UserEntity> isEmailExist = userRepository.findByEmail(signupDTO.getEmail());
        if (isEmailExist.isPresent()) throw new UserException(UserErrorCode.EXIST_ID);
        Optional<UserEntity> isNickNameExist = userRepository.findByNickname(signupDTO.getNickname());
        if (isNickNameExist.isPresent()) throw new UserException(UserErrorCode.EXIST_NICKNAME);
        UserEntity userEntity = new UserEntity(signupDTO);
        userEntity.setUserPassword(passwordEncoder.encode(signupDTO.getPassword()));
        int num =0;
    	if(signupDTO.getGender().equals("남성")) {
    		 num = (int)(Math.random() * 9) + 1;
    		userEntity.setUserSex("남성");
    	}
    	else {
    		 num = (int)(Math.random() * 6) + 10;
    		userEntity.setUserSex("여성");
    	}
        userEntity.setUserImg("/uploads/classicImage" + num + ".png");
        try {
        userRepository.save(userEntity);
        }catch (DataIntegrityViolationException e) {
        	throw new UserException(UserErrorCode.UNKNOWN);
		}
    }

    public void loginUser(SignupLoginDTO dto) {
        UserEntity user = utils.findUser(dto.getEmail());
    }

    public UserEntity expUp(String email) {
        UserEntity user = utils.findUser(email);
        int exp = user.getUserExp() + 20;

        if (exp >= 100) {
            user.setUserLevel(user.getUserLevel() + 1);
            user.setUserExp(0);
            if (user.getUserLevel() >= 10) user.setUserGrade(Grade.VIP);
            else if (user.getUserLevel() >= 7) user.setUserGrade(Grade.GOLD);
            else if (user.getUserLevel() >= 4) user.setUserGrade(Grade.SILVER);
        } else {
            user.setUserExp(exp);
        }

        log.info("💾 경험치/레벨 저장 - email: {}", email);
        return userRepository.save(user);
    }

    public void logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("access", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh", null);
        refreshCookie.setPath("/auth");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    public void deleteUser(String email, String pwd) {
        UserEntity user = utils.findUser(email);
        if (!(utils.isSocialAccount(email)) && !passwordEncoder.matches(pwd, user.getUserPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        log.info("🗑️ 유저 삭제 - email: {}", email);
        userRepository.deleteByUserEmail(email);
    }

    public String findId(UserInfoDTO dto) {
        log.info("🔍 이름으로 유저 검색 - name: {}", dto.getUserName());
        UserEntity user = userRepository.findByName(dto.getUserName());
        if (user == null) throw new UserException(UserErrorCode.INVALID_ID);
        if (!user.getUserBirthDay().equals(dto.getUserBirthDay())) {
            throw new UserException(UserErrorCode.INVALID_BIRTH_DAY);
        }
        if (utils.isSocialAccount(dto.getUserEmail())) {
            throw new UserException(UserErrorCode.ISSOCIAL);
        }
        return user.getUserEmail();
    }

    public String findPwd(UserInfoDTO dto) {
        if (utils.isSocialAccount(dto.getUserEmail())) {
            throw new UserException(UserErrorCode.ISSOCIAL);
        }
        UserEntity user = utils.findUser(dto.getUserEmail());
        if (!user.getUserName().equals(dto.getUserName())) {
            throw new UserException(UserErrorCode.INVALID_NAME);
        }

        String newPassword = createKey();
        user.setUserPassword(passwordEncoder.encode(newPassword));

        log.info("🔐 비밀번호 재설정 저장 - email: {}", user.getUserEmail());
        userRepository.save(user);
        return newPassword;
    }

    public UserEntity updateUser(ProfileEditDTO dto, String email) {
        UserEntity user = utils.findUser(email);

        if (!utils.isSocialAccount(user.getUserEmail())) {
            if (user.getUserPassword() == null || !passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword())) {
                throw new UserException(UserErrorCode.INVALID_PASSWORD);
            }
        }

        if (dto.getNickName() != null) {
        	 Optional<UserEntity> isNickNameExist = userRepository.findByNickname(dto.getNickName());
             if (isNickNameExist.isPresent()) throw new UserException(UserErrorCode.EXIST_NICKNAME);
        	user.setUserNickName(dto.getNickName());
        }
        if (dto.getUserInfo() != null) 
        	user.setUserInfo(dto.getUserInfo());
        if (dto.getUserImg() != null) 
                user.setUserImg(utils.saveImage(dto.getUserImg()));
        if (dto.getSido() != null || dto.getSigungu() != null) 
            user.setAddress(new Address(dto.getSido(), dto.getSigungu()));
        if(dto.getUserNewPassword()!=null)
        	user.setUserPassword(passwordEncoder.encode(dto.getUserNewPassword()));

        return userRepository.save(user);
    }

    public UserEntity findUserSync(String email) {
        UserEntity user = utils.findUser(email);

        if (user.getUserGrade() == Grade.BANNED && user.getReportedDate() != null) {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime reportedDate = user.getReportedDate();
            long daysBetween = ChronoUnit.DAYS.between(reportedDate, today);

            if (daysBetween >= 30) {
                int level = user.getUserLevel();
                if (level >= 10) user.setUserGrade(Grade.VIP);
                else if (level >= 7) user.setUserGrade(Grade.GOLD);
                else if (level >= 4) user.setUserGrade(Grade.SILVER);
                else user.setUserGrade(Grade.BRONZE);
                user.setReportedDate(null);

                log.info("🔓 정지 해제 및 등급 복원 - email: {}", email);
                return userRepository.save(user);
            }
        }

        return user;
    }
}
