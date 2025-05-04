package pProject.pPro.User;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ServiceUtils utils;
    private final RedisTemplate<String, String> redisTemplate;

    public UserService(
        UserRepository userRepository,
        BCryptPasswordEncoder passwordEncoder,
        ServiceUtils utils,
        @Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.utils = utils;
        this.redisTemplate = redisTemplate;
    }

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
        if (userRepository.findByEmail(signupDTO.getEmail()).isPresent())
            throw new UserException(UserErrorCode.EXIST_ID);

        if (userRepository.findByNickname(signupDTO.getNickname()).isPresent())
            throw new UserException(UserErrorCode.EXIST_NICKNAME);

        UserEntity userEntity = new UserEntity(signupDTO);
        userEntity.setUserPassword(passwordEncoder.encode(signupDTO.getPassword()));

        int num = signupDTO.getGender().equals("남성")
            ? (int) (Math.random() * 9) + 1
            : (int) (Math.random() * 6) + 10;
        userEntity.setUserSex(signupDTO.getGender());
        userEntity.setUserImg("/uploads/classicImage/" + num + ".png");

        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserException(UserErrorCode.UNKNOWN);
        }
    }

    public void loginUser(SignupLoginDTO dto) {
        utils.findUser(dto.getEmail()); // 로그인 처리 로직은 따로 필요 시 구현
    }

    public UserEntity expUp(String email) {
        UserEntity user = utils.findUser(email);
        int exp = user.getUserExp() + 20;
        user.expUp();
        return userRepository.save(user);
    }

    public void logout(String email, HttpServletResponse response) {
        redisTemplate.delete("refresh:" + email);
        response.addCookie(deleteCookie("access", "/"));
        response.addCookie(deleteCookie("refresh", "/auth"));
    }

    public void deleteUser(String email, String pwd) {
        UserEntity user = utils.findUser(email);
        if (!utils.isSocialAccount(email) && !passwordEncoder.matches(pwd, user.getUserPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
        userRepository.deleteByUserEmail(email);
    }

    public String findId(UserInfoDTO dto) {
        UserEntity user = userRepository.findByName(dto.getUserName());
        if (user == null || !user.getUserBirthDay().equals(dto.getUserBirthDay()))
            throw new UserException(UserErrorCode.INVALID_ID);

        if (utils.isSocialAccount(dto.getUserEmail()))
            throw new UserException(UserErrorCode.ISSOCIAL);

        return user.getUserEmail();
    }

    public String findPwd(UserInfoDTO dto) {
        if (utils.isSocialAccount(dto.getUserEmail()))
            throw new UserException(UserErrorCode.ISSOCIAL);

        UserEntity user = utils.findUser(dto.getUserEmail());
        if (!user.getUserName().equals(dto.getUserName()))
            throw new UserException(UserErrorCode.INVALID_NAME);

        String newPassword = createKey();
        user.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return newPassword;
    }

    public UserEntity updateUser(ProfileEditDTO dto, String email) {
        UserEntity user = utils.findUser(email);

        if (!utils.isSocialAccount(user.getUserEmail()) &&
            (user.getUserPassword() == null || !passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword()))) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        if (dto.getNickName() == null)
            throw new UserException(UserErrorCode.REQUIRED_NICKNAME);

        if (!dto.getNickName().equals(user.getUserNickName()) &&
            userRepository.findByNickname(dto.getNickName()).isPresent()) {
            throw new UserException(UserErrorCode.EXIST_NICKNAME);
        }

        user.setUserNickName(dto.getNickName());
        if (dto.getUserInfo() != null) user.setUserInfo(dto.getUserInfo());
        if (dto.getUserImg() != null) user.setUserImg(saveImage(dto.getUserImg()));
        if (dto.getSido() != null || dto.getSigungu() != null)
            user.setAddress(new Address(dto.getSido(), dto.getSigungu()));
        if (dto.getUserNewPassword() != null)
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
            long daysBetween = ChronoUnit.DAYS.between(user.getReportedDate(), LocalDateTime.now());

            if (daysBetween >= 30) {
                int level = user.getUserLevel();
                if (level >= 10) user.setUserGrade(Grade.VIP);
                else if (level >= 7) user.setUserGrade(Grade.GOLD);
                else if (level >= 4) user.setUserGrade(Grade.SILVER);
                else user.setUserGrade(Grade.BRONZE);

                user.setReportedDate(null);
                return userRepository.save(user);
            }
        }

        return user;
    }

    public UserDetailDTO getUserInfo(Long userId) {
        return userRepository.getUserInfo(userId)
            .map(UserDetailDTO::new)
            .orElseThrow(() -> new UserException(UserErrorCode.INVALID_ID));
    }

    public String saveImage(MultipartFile imageFile) {
        String UPLOAD_DIR = "/home/ubuntu/uploads/";
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        File savedFile = new File(UPLOAD_DIR + savedFileName);

        try {
            imageFile.transferTo(savedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "/uploads/" + savedFileName;
    }

    private Cookie deleteCookie(String name, String path) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }
}
