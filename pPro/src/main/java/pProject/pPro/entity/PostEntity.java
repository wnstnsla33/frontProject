package pProject.pPro.entity;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.post.DTO.WritePostDTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "post_id")
    private Long postId;
	
	private String title;
	private String titleImg;
	private String content;
	
	private LocalDate createDate;
	
	private LocalDate modifiedDate;
	
	private int viewCount;
	
	//bookmarkCount,likeCount는 DTO에서
	private int bookmarkCount;
	private String secreteKey; //널일 경우 일반 글
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	@JsonManagedReference("user-post")
	private UserEntity user;

	public PostEntity(WritePostDTO writePostDTO,UserEntity user) {
		super();
		this.title = writePostDTO.getTitle();
		try {
		this.titleImg = saveImage(writePostDTO.getTitleImg());
		}catch (IOException e) {
			System.out.println("title이미지 오류");
		}
		this.content = writePostDTO.getContent();
		this.createDate = LocalDate.now();
		this.modifiedDate = LocalDate.now();
		this.viewCount = 0;
		this.bookmarkCount = 0;
		this.secreteKey = writePostDTO.getSecreteKey();
		this.user = user;
	}
	public String saveImage(MultipartFile imageFile) throws IOException {
	    // 저장할 폴더 경로 (원하는 경로로 수정 가능)
	    String uploadDir = "C:/myproject/uploads/";
	    File dir = new File(uploadDir);
	    if (!dir.exists()) dir.mkdirs();  // 폴더가 없으면 생성

	    // 파일명 중복 방지를 위해 UUID 추가
	    String originalFilename = imageFile.getOriginalFilename();
	    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    String savedFileName = UUID.randomUUID() + extension;

	    // 실제 저장
	    File savedFile = new File(uploadDir + savedFileName);
	    imageFile.transferTo(savedFile);

	    // 저장된 파일 경로나 URL을 반환 (프로젝트에 따라 달라짐)
	    return "/uploads/" + savedFileName;  // 프론트에서 접근 가능한 경로로 반환
	}
}
