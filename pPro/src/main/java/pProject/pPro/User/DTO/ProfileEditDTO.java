package pProject.pPro.User.DTO;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditDTO {
	@NotNull(message= "닉네임을 적어주세요")
    private String nickName;
    private MultipartFile userImg;
    private String userInfo;
    private String userPassword;
    private String userNewPassword;
    private String sido;
	private String sigungu;
	private LocalDate birthDay;
}
