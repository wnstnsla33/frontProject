package pProject.pPro.post;

import java.io.File;
import java.io.IOException;
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

	public ResponseEntity writePost(WritePostDTO writePostDTO, String email) {
		UserEntity user = userRepository.findByEmail(email);
		PostEntity newPost = new PostEntity(writePostDTO, user);
		postRepository.save(newPost);
		return postResponseDTO.postSuccess("정상적으로 등록되었습니다");
	}

}
