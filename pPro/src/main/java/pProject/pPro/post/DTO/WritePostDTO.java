package pProject.pPro.post.DTO;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WritePostDTO {
    private String title;
    private String content;
    private MultipartFile titleImg;
    private String secreteKey;
}
