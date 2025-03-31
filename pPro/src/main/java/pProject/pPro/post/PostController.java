package pProject.pPro.post;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pProject.pPro.User.UserService;
import pProject.pPro.entity.ImageStorageService;
import pProject.pPro.post.DTO.PassWordDTO;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostControllerDTO;
import pProject.pPro.post.DTO.PostServiceDTO;
import pProject.pPro.post.DTO.WritePostDTO;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final UserService userService;
	private final ImageStorageService imageStorageService;
	void makeMessage(String methodName) {
		System.out.println("********************************" + methodName);
	}

	@PostMapping("/post/new") // 게시물 등록
	public ResponseEntity<?> newPost(@ModelAttribute WritePostDTO writePostDTO,
			@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("newPost");
		String email = loginUser.getUsername();
		userService.expUp(email);
		PostServiceDTO<String> result = postService.writePost(writePostDTO, email);
		return result.isSuccess() ? PostControllerDTO.postSuccess(result.getMsg()) : PostControllerDTO.postFail(result.getMsg());
	}
	@PostMapping("/post/image")
	public ResponseEntity imageUpload(@RequestPart("image") MultipartFile imageFile) {
		if(imageFile.isEmpty()) {
			 return PostControllerDTO.postFail("이미지 값이 없습니다.");
		}
		String imageUrl = imageStorageService.saveImage(imageFile);
		return PostControllerDTO.postSuccess(imageUrl);
	}

	@GetMapping("/post") // 게시물들(paging)
	public ResponseEntity<?> postList(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "sortType", defaultValue = "1") int sortNumber) {
		makeMessage("post");
		PostServiceDTO<?> result = postService.getPostList(page - 1, sortNumber);
		return result.isSuccess() ? PostControllerDTO.getPostList((List<PostListDTO>) result.getData(), postService.postCount().getData()) : PostControllerDTO.postFail(result.getMsg());
	}

	@GetMapping("/post/mybookmark") // 게시물들(paging)
	public ResponseEntity<?> bookmarkPostList(@AuthenticationPrincipal UserDetails user,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "sortType", defaultValue = "1") int sortNumber) {
		makeMessage("post/bookmark");
		PostServiceDTO<?> result = postService.getPostBookmarkList(user.getUsername(), page - 1, sortNumber);
		return result.isSuccess() ? PostControllerDTO.getPostList((List<PostListDTO>) result.getData(), postService.postBookmarkCount(user.getUsername()).getData()) : PostControllerDTO.postFail(result.getMsg());
	}

	@GetMapping("/post/{postId}") // 게시물 상세
	public ResponseEntity<?> getPostDetail(@PathVariable("postId") long postId,
			@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("post/postId");
		String email = (loginUser != null) ? loginUser.getUsername() : null;
		PostServiceDTO<PostListDTO> result = postService.incrementAndGetPost(postId, email);
		return result.isSuccess() ? PostControllerDTO.getPost(result.getData()) : PostControllerDTO.postFail(result.getMsg());
	}

	@PutMapping("/post/{postId}") // 게시물 수정
	public ResponseEntity<?> updatePost(@PathVariable("postId") long postId,
			@RequestBody PostListDTO postListDTO, @AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("update post/postId");
		PostServiceDTO<PostListDTO> result = postService.updatePost(postId, postListDTO, loginUser.getUsername());
		return result.isSuccess() ? PostControllerDTO.getPost(result.getData()) : PostControllerDTO.postFail(result.getMsg());
	}

	@DeleteMapping("/post/{postId}") // 게시물 삭제
	public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,
			@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("delete post/postId");
		PostServiceDTO<String> result = postService.deletePost(postId, loginUser.getUsername());
		return result.isSuccess() ? PostControllerDTO.postSuccess(result.getMsg()) : PostControllerDTO.postFail(result.getMsg());
	}

	@PostMapping("/post/secrete/{postId}") // 비밀 게시물
	public ResponseEntity<?> getSecretePost(@PathVariable("postId") Long postId,
			@RequestBody PassWordDTO passWordDTO) {
		makeMessage("secrete post/postId" + postId + passWordDTO.getPwd());
		PostServiceDTO<PostListDTO> result = postService.getSecretePost(postId, passWordDTO.getPwd());
		return result.isSuccess() ? PostControllerDTO.getPost(result.getData()) : PostControllerDTO.postFail(result.getMsg());
	}

	@GetMapping("/post/myPost") // 내가 쓴 게시물들
	public ResponseEntity<?> getMyPostLists(@AuthenticationPrincipal UserDetails user) {
		makeMessage("내가 쓴 게시물 보기");
		PostServiceDTO<?> result = postService.getMyPostList(user.getUsername());
		return result.isSuccess() ? PostControllerDTO.getPostList((List<PostListDTO>) result.getData()) : PostControllerDTO.postFail(result.getMsg());
	}

//	@GetMapping("/post/test")
//	public void testest(@RequestParam(value = "postId") Long postId) {
//		postService.findPostTest(postId);
//	}
	@GetMapping("/post/topView")
	public ResponseEntity<?> getTop10Post(){
		PostServiceDTO<List<PostListDTO>> list = postService.getTop10Posts();
		return PostControllerDTO.getPostList(list.getData());
	}
}