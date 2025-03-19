package pProject.pPro.post;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pProject.pPro.User.UserRepository;
import pProject.pPro.User.UserService;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostResponseDTO;
import pProject.pPro.post.DTO.WritePostDTO;

@Service
@Transactional
public class PostService {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;

	private PostResponseDTO postResponseDTO;

	public ResponseEntity getPostList() {
		List<PostEntity> postEntities = postRepository.findAll();
		System.out.println("**************************");
		// Entity → DTO 변환
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new) // PostListDTO 생성자 사용
				.collect(Collectors.toList());
		return postResponseDTO.getPostList(postList);
	}
	public ResponseEntity getPostList(String email) {
		List<PostEntity> postEntities = postRepository.findAll();
		System.out.println("**************************");
		// Entity → DTO 변환
		List<PostListDTO> postList = postEntities.stream().map(PostListDTO::new) // PostListDTO 생성자 사용
				.collect(Collectors.toList());
		return postResponseDTO.getPostList(postList);
	}

	public ResponseEntity writePost(WritePostDTO writePostDTO, String email) {
		UserEntity user = userRepository.findByEmail(email).get();
		PostEntity newPost = new PostEntity(writePostDTO, user);
		postRepository.save(newPost);
		return postResponseDTO.postSuccess("정상적으로 등록되었습니다");
	}
//	public ResponseEntity getPostDetail(Long postId) {
//		return postResponseDTO.getPost(new PostListDTO((postRepository.getPostDetail(postId).orElseThrow(
//				() -> new RuntimeException("게시글이 존재하지 않습니다.")))));
//	}
	public PostEntity incrementBookmarkCount(Long postId,boolean isBookmarked){
		PostEntity post = postRepository.findById(postId).orElseThrow(
				() -> new RuntimeException("게시글이 존재하지 않습니다."));
		if(isBookmarked)post.setBookmarkCount(post.getBookmarkCount()+1);
		else post.setBookmarkCount(post.getBookmarkCount()-1);
		return post;
	}
	public ResponseEntity incrementAndGetPost(Long postId) {
	    PostEntity post = postRepository.findById(postId)
	        .orElseThrow(() -> new RuntimeException("게시글 없음"));
	    post.setViewCount(post.getViewCount() + 1);
	    return PostResponseDTO.getPost(new PostListDTO(post));
	}
	public ResponseEntity updatePost(Long postId,PostListDTO postListDTO,String email) {
		PostEntity post = postRepository.findById(postId).get();
		UserEntity user = userRepository.findByEmail(email).get();
		if(post.getUser().getUserId()!=user.getUserId()) return null;
		else {
			post.setContent(postListDTO.getContent());
			post.setModifiedDate(LocalDate.now());
			post.setTitle(postListDTO.getTitle());
			post.setTitleImg(postListDTO.getTitleImg());
		}
		return postResponseDTO.getPost(new PostListDTO(post));
	}
	public ResponseEntity deletePost(Long postId,String email) {
		PostEntity post = postRepository.findById(postId).get();
		UserEntity user = userRepository.findByEmail(email).get();
		if(post.getUser().getUserId()!=user.getUserId()) return null;
		else {
			postRepository.delete(post);
			return postResponseDTO.postSuccess("정상 삭제 완료");
		}
	}
	
}
