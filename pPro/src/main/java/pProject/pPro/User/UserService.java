package pProject.pPro.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.EntityUtils;
import pProject.pPro.User.DTO.ProfileEditDTO;
import pProject.pPro.User.DTO.SignupLoginDTO;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.User.Exception.UserErrorCode;
import pProject.pPro.User.Exception.UserException;
import pProject.pPro.entity.Address;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.ImageStorageService;
import pProject.pPro.entity.UserEntity;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageStorageService imageStorageService;
    private final EntityUtils utils;
    public String createKey() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void signup(SignupLoginDTO signupDTO) {
        Optional<UserEntity> isExist = userRepository.findByEmail(signupDTO.getEmail());
        if (isExist.isPresent()) throw new UserException(UserErrorCode.EXIST_ID);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserEmail(signupDTO.getEmail());
        userEntity.setUserNickName(signupDTO.getNickname());
        userEntity.setUserName(signupDTO.getRealName());
        userEntity.setUserBirthDay(signupDTO.getBirthDate().toString());
        userEntity.setReportedCount(0);
        int age = Period.between(signupDTO.getBirthDate(), LocalDate.now()).getYears();
        userEntity.setUserAge(age);
        userEntity.setUserSex(signupDTO.getGender());
        userEntity.setAddress(new Address(signupDTO.getSido(), signupDTO.getSigungu()));
        userEntity.setUserGrade(Grade.BRONZE);
        userEntity.setUserCreateDate(LocalDate.now().toString());
        userEntity.setUserPassword(passwordEncoder.encode(signupDTO.getPassword()));
        int num = (int) (Math.random() * 15) + 1;
        userEntity.setUserImg("/uploads/" + num + ".png");
        userEntity.setUserLevel(1);
        userEntity.setUserExp(0);
        userRepository.save(userEntity);
    }

    public void loginUser(SignupLoginDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new UserException(UserErrorCode.NO_EXIST_ID));

        if (!passwordEncoder.matches(dto.getPassword(), user.getUserPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
    }

    public UserEntity expUp(String email) {
        UserEntity user = userRepository.findByEmail(email).get();
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
        userRepository.deleteByUserEmail(email);
    }

    public String findId(UserInfoDTO dto) {
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

        UserEntity user = userRepository.findByEmail(dto.getUserEmail())
            .orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL));

        if (!user.getUserName().equals(dto.getUserName())) {
            throw new UserException(UserErrorCode.INVALID_NAME);
        }

        String newPassword = createKey();
        user.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return newPassword;
    }

    public UserEntity updateUser(ProfileEditDTO dto, String email) {
        UserEntity user = utils.findUser(email);

        if (!utils.isSocialAccount(user.getUserEmail())) {
            if (user.getUserPassword() == null || !passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword())) {
                return null;
            }
        }

        if (dto.getNickName() != null) user.setUserNickName(dto.getNickName());
        if (dto.getUserInfo() != null) user.setUserInfo(dto.getUserInfo());
        if (dto.getUserImg() != null) {
            try {
                user.setUserImg(imageStorageService.saveImage(dto.getUserImg()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dto.getSido() != null || dto.getSigungu() != null) {
            user.setAddress(new Address(dto.getSido(), dto.getSigungu()));
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
            }
        }
        return user;
    }

    

   

    
}
