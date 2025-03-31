package pProject.pPro.post.DTO;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WritePostDTO {
	@NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;
	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 10000, message = "내용은 최대 10000자까지 입력 가능합니다.")
    private String content;
    private String secreteKey;
}
