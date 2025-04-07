package pProject.pPro.post.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchDTO {
    private int page = 0;
    private int sortType = 1;
    private String keyword = "";
}
