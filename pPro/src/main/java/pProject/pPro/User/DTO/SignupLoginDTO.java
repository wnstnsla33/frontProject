package pProject.pPro.User.DTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class SignupLoginDTO {
	private String email;
	private String password;
	private String nickname;
	private String realName;
	private LocalDate birthDate;
	private String gender;
}
