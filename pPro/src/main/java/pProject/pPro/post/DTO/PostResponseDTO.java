package pProject.pPro.post.DTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import pProject.pPro.entity.PostEntity;

@Getter
public class PostResponseDTO {
	private int state;
	private String messsage;
	
	public static ResponseEntity regPostSuccess() {
    	return ResponseEntity.status(HttpStatus.OK).body("게시물이 정상 등록되었습니다.");
    }
	
	public static ResponseEntity getPostList(List<PostListDTO> list) {
    	return ResponseEntity.status(HttpStatus.OK).body(list);
    }
	
	public static ResponseEntity getPost(PostListDTO postListDTO) {
    	return ResponseEntity.status(HttpStatus.OK).body(postListDTO);
    }
	//msg
	public static ResponseEntity postFail(String msg) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
	public static ResponseEntity postSuccess(String msg) {
    	return ResponseEntity.status(HttpStatus.OK).body(msg);
    }
}
