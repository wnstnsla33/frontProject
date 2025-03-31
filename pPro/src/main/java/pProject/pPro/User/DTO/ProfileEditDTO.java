package pProject.pPro.User.DTO;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditDTO {
    private String nickName;
    private MultipartFile userImg;
    private String userInfo;
    private String userPassword;
    private String userNewPassword;
    private String sido;
	private String sigungu;
}
