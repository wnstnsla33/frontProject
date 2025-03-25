package pProject.pPro.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pProject.pPro.User.UserService;
import pProject.pPro.post.DTO.PassWordDTO;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostResponseDTO;
import pProject.pPro.post.DTO.WritePostDTO;

@RestController
public class PostController {
	private PostResponseDTO postResponseDTO;
	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;
	void makeMessage(String methodName) {
		System.out.println("********************************" + methodName);
	}
	@PostMapping("/post/new")//게시물 등록
	public ResponseEntity newPost(@ModelAttribute WritePostDTO writePostDTO,
			@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("newPost");
		String email = loginUser.getUsername();
		userService.expUp(email);
		return postService.writePost(writePostDTO, email);
	}


	@GetMapping("/post")//게시물들(paging)
	public ResponseEntity postList(@RequestParam(value = "page", defaultValue = "0") int page,@RequestParam(value ="sortType",defaultValue = "1") int sortNumber){
		makeMessage("post");
		return postResponseDTO.getPostList(postService.getPostList(page-1,sortNumber),postService.postCount());
	}
	@GetMapping("/post/mybookmark")//게시물들(paging)
	public ResponseEntity bookmarkPostList(@AuthenticationPrincipal UserDetails user, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value ="sortType",defaultValue = "1") int sortNumber) {
		makeMessage("post/bookmark");
		return postResponseDTO.getPostList(postService.getPostBookmarkList(user.getUsername(), page-1,sortNumber),postService.postBookmarkCount(user.getUsername()));
	}
	@GetMapping("/post/{postId}")//게시물 상세
	public ResponseEntity getPostDetail(@PathVariable("postId") long postId,@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("post/postId");
		String email = loginUser.getUsername();
		return postResponseDTO.getPost(postService.incrementAndGetPost(postId, email));
		
	}
	@PutMapping("/post/{postId}")//해당 게시물 수정
	public ResponseEntity updatePost(@PathVariable("postId") long postId ,@RequestBody PostListDTO postListDTO,@AuthenticationPrincipal UserDetails loginuser) {
		makeMessage("update post/postId");
		return postResponseDTO.getPost(postService.updatePost(postId,postListDTO,loginuser.getUsername()));
	}
	@DeleteMapping("/post/{postId}")//게시물 삭제
	public ResponseEntity deletePost(@PathVariable("postId")Long postId,@AuthenticationPrincipal UserDetails loginuser) {
		makeMessage("delete post/postId");
		boolean isDeleted = postService.deletePost(postId, loginuser.getUsername());
		if(isDeleted)return postResponseDTO.postSuccess("정상 삭제 완료");
		else return postResponseDTO.postFail("잘못된 요청입니다");
	}
	
	@PostMapping("/post/secrete/{postId}")//비밀 게시물(아직 구현X)
	public ResponseEntity getSecretePost(@PathVariable("postId")Long postId,@RequestBody PassWordDTO passWordDTO) {
		makeMessage("secrete post/postId"+postId+passWordDTO.getPwd());
		PostListDTO postListDTO = postService.getSecretePost(postId,passWordDTO.getPwd());
		if(postListDTO==null)return postResponseDTO.postFail("잘못된 비밀번호 입니다");
		else return postResponseDTO.getPost(postListDTO);
	}
	@GetMapping("/post/myPost")//내가쓴 게시물들
	public ResponseEntity getMyPostLists(@AuthenticationPrincipal UserDetails user) {
		makeMessage("내가 쓴 게시물 보기");
		return postResponseDTO.getPostList(postService.getMyPostList(user.getUsername()));
	}
	@GetMapping("/post/test")
	public void testest(@RequestParam(value = "postId") Long postId) {
		postService.findPostTest(postId);
	}
}
