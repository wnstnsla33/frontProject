package pProject.pPro.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
import pProject.pPro.User.DTO.ProfileEditDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserDetailDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.entity.Address;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
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
        userEntity.setUserImg("/uploads/classicImage/" + num + ".png");
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
        if (dto.getBirthDay() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setUserBirthDay(dto.getBirthDay().format(formatter));
        }
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
    public UserDetailDTO getUserInfo(Long userId) {
    	Optional<UserEntity> user = userRepository.getUserInfo(userId);
    	if(user.isPresent())return new UserDetailDTO(user.get());
    	else throw new UserException(UserErrorCode.INVALID_ID);
    }
    List<SignupLoginDTO> makeuser(){
    	List<SignupLoginDTO> signupList = List.of(
    		    new SignupLoginDTO("hyunwoo1@gmail.com", "rlawnstn1234!", "서울하늘", "김현우", 28, LocalDate.of(1996, 3, 15), "M", "서울", "강남구"),
    		    new SignupLoginDTO("junho2@gmail.com", "rlawnstn1234!", "강북호랑이", "이준호", 31, LocalDate.of(1993, 7, 22), "M", "서울", "노원구"),
    		    new SignupLoginDTO("eunji3@gmail.com", "rlawnstn1234!", "은지로그", "박은지", 27, LocalDate.of(1997, 2, 19), "F", "서울", "성동구"),
    		    new SignupLoginDTO("minji4@gmail.com", "rlawnstn1234!", "민지로그", "한민지", 28, LocalDate.of(1996, 9, 25), "F", "서울", "성북구"),
    		    new SignupLoginDTO("taeyang5@gmail.com", "rlawnstn1234!", "야경러버", "박태양", 26, LocalDate.of(1998, 1, 9), "M", "서울", "서대문구"),
    		    new SignupLoginDTO("sumin6@gmail.com", "rlawnstn1234!", "수민수민", "김수민", 25, LocalDate.of(1999, 8, 5), "F", "서울", "송파구"),
    		    new SignupLoginDTO("donghyun7@gmail.com", "rlawnstn1234!", "동작킴", "최동현", 29, LocalDate.of(1995, 11, 30), "M", "서울", "동작구"),
    		    new SignupLoginDTO("jihye8@gmail.com", "rlawnstn1234!", "지혜의하루", "이지혜", 29, LocalDate.of(1995, 12, 3), "F", "서울", "마포구"),
    		    new SignupLoginDTO("yujin9@gmail.com", "rlawnstn1234!", "유진유진", "정유진", 31, LocalDate.of(1993, 5, 14), "F", "서울", "중구"),
    		    new SignupLoginDTO("minsoo10@gmail.com", "rlawnstn1234!", "피트니스민수", "정민수", 32, LocalDate.of(1992, 6, 14), "M", "서울", "관악구"),
    		    new SignupLoginDTO("gyumin11@gmail.com", "rlawnstn1234!", "사당규민", "한규민", 25, LocalDate.of(1999, 12, 27), "M", "서울", "동대문구"),
    		    new SignupLoginDTO("seungho12@gmail.com", "rlawnstn1234!", "서울승호", "배승호", 30, LocalDate.of(1994, 4, 18), "M", "서울", "양천구"),
    		    new SignupLoginDTO("jinhwan13@gmail.com", "rlawnstn1234!", "고척진화니", "오진환", 27, LocalDate.of(1997, 9, 3), "M", "서울", "구로구"),
    		    new SignupLoginDTO("yena14@gmail.com", "rlawnstn1234!", "예나예나", "유예나", 26, LocalDate.of(1998, 5, 20), "F", "서울", "영등포구"),
    		    new SignupLoginDTO("miso15@gmail.com", "rlawnstn1234!", "미소가득", "서미소", 24, LocalDate.of(2000, 1, 17), "F", "서울", "광진구"),
    		    new SignupLoginDTO("doyoung16@gmail.com", "rlawnstn1234!", "도영일기", "윤도영", 29, LocalDate.of(1995, 8, 30), "M", "서울", "은평구"),
    		    new SignupLoginDTO("haeun17@gmail.com", "rlawnstn1234!", "하은하루", "이하은", 27, LocalDate.of(1997, 11, 8), "F", "서울", "종로구"),
    		    new SignupLoginDTO("seoyoon18@gmail.com", "rlawnstn1234!", "서윤서윤", "정서윤", 25, LocalDate.of(1999, 6, 4), "F", "서울", "도봉구"),
    		    new SignupLoginDTO("taeseok19@gmail.com", "rlawnstn1234!", "태석태석", "장태석", 30, LocalDate.of(1994, 2, 12), "M", "서울", "강서구"),
    		    new SignupLoginDTO("jihoon20@gmail.com", "rlawnstn1234!", "지훈이", "홍지훈", 28, LocalDate.of(1996, 10, 6), "M", "서울", "금천구")
    		);
    	return signupList;
    }
    public void insertDummyUsers() {
        List<SignupLoginDTO> signupList = makeuser();

        for (SignupLoginDTO dto : signupList) {
            try {
                signup(dto);
                System.out.println("가입 완료: " + dto.getEmail());
            } catch (UserException e) {
                System.out.println("중복 또는 오류로 실패: " + dto.getEmail() + " → " + e.getErrorCode());
            } catch (Exception e) {
                System.out.println("예상치 못한 오류: " + dto.getEmail());
            }
        }
    }

}
