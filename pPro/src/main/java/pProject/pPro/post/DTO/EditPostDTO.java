package pProject.pPro.post.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pProject.pPro.entity.PostEntity;
@Getter

public class EditPostDTO {
	private String title;
	private String content;
	public EditPostDTO(PostEntity post) {
		super();
		this.title = post.getTitle();
		this.content = post.getContent();
	}
	
	
}
