package pProject.pPro.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pProject.pPro.User.UserService;
import pProject.pPro.post.DTO.WritePostDTO;

@RestController
public class PostController {

	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;
	@PostMapping("/post/new")
	public ResponseEntity newPost(@ModelAttribute WritePostDTO writePostDTO,
			@AuthenticationPrincipal UserDetails loginUser) {
		String email =loginUser.getUsername();
		userService.expUp(email);
		return postService.writePost(writePostDTO, email);
	}
	@GetMapping("/post")
	public ResponseEntity postList() {
		return postService.getPostList();
	}
}
