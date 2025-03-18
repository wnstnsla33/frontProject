package pProject.pPro.User.DTO;
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
    private String userImg;
    private String userInfo;
    private String userPassword;
    private String userNewPassword;
}
