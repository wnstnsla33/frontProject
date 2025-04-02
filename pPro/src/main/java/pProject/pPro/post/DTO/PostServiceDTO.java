package pProject.pPro.post.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostServiceDTO<T> {
	private boolean success;
	private String msg;
	private T data;
}

