package pProject.pPro.message.DTO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class SaveMessageDTO {
	@NotNull
	@NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    private String content;


    @NotNull(message = "받는 사람 ID는 필수입니다.")
    private Long receiverId;
    
    @NotNull(message = "타입 설정은 필수입니다.")
    private MessageType type;
}
