package pProject.pPro.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pProject.pPro.User.UserService;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.WritePostDTO;

@RestController
public class PostController {

	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;
	void makeMessage(String methodName) {
		System.out.println("********************************" + methodName);
	}
	@PostMapping("/post/new")
	public ResponseEntity newPost(@ModelAttribute WritePostDTO writePostDTO,
			@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("newPost");
		String email = loginUser.getUsername();
		userService.expUp(email);
		return postService.writePost(writePostDTO, email);
	}

	@GetMapping("/post")
	public ResponseEntity postList() {
		makeMessage("/post");
		return postService.getPostList();
	}

	@GetMapping("/post/user")
	public ResponseEntity postList(@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("post/user");
		return postService.getPostList();
	}

	@GetMapping("/post/{postId}")
	public ResponseEntity getPostDetail(@PathVariable("postId") long postId) {
		makeMessage("post/postId");
		return postService.incrementAndGetPost(postId);
	}
	@PutMapping("/post/{postId}")
	public ResponseEntity updatePost(@PathVariable("postId") long postId ,@RequestBody PostListDTO postListDTO,@AuthenticationPrincipal UserDetailsImpl userDetails) {
		makeMessage("update post/postId");
		
	}
}
