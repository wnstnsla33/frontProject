package pProject.pPro.User.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class SignupLoginDTO {
	@Email(message= "email형식으로 적어주세요")
	@NotNull(message= "필수 값을 적어주세요")
	private String email;
	@NotNull(message= "필수 값을 적어주세요")
	private String password;
	@NotNull(message= "필수 값을 적어주세요")
	private String nickname;
	@NotNull(message= "필수 값을 적어주세요")
	private String realName;
	@Min(value = 15,message = "15세 이상만 가입할 수 있습니다.")
	private int age;
	@NotNull(message= "필수 값을 적어주세요")
	private LocalDate birthDate;
	@NotNull(message= "필수 값을 적어주세요")
	private String gender;
	@NotNull(message= "필수 값을 적어주세요")
	private String sido;
	@NotNull(message= "필수 값을 적어주세요")
	private String sigungu;
}
