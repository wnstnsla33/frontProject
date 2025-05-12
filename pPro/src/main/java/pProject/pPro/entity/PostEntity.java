package pProject.pPro.entity;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "post",
indexes = {
  @Index(name = "idx_post_user", columnList = "user_id")
}
)
public class PostEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "post_id")
    private Long postId;
	
	private String title;
	@Lob
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private LocalDateTime createDate;
	
	private LocalDateTime modifiedDate;
	
	private int viewCount;
	private int replyCount;
	//bookmarkCount,likeCount는 DTO에서
	private int bookmarkCount;
	private String secreteKey; //널일 경우 일반 글
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	@JsonManagedReference("user-post")
	private UserEntity user;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("post-bookmark")
	private List<BookmarkEntity> bookmark = new ArrayList<BookmarkEntity>();
	
	public PostEntity(WritePostDTO writePostDTO,UserEntity user) {
		super();
		this.title = writePostDTO.getTitle();
		this.content = writePostDTO.getContent();
		this.createDate = LocalDateTime.now();
		this.modifiedDate = LocalDateTime.now();
		this.viewCount = 0;
		this.bookmarkCount = 0;
		this.secreteKey = writePostDTO.getSecreteKey();
		this.user = user;
	}
	public void increaseReplyCount() {
		this.replyCount = replyCount+1;
	}
	public void decreaseReplyCount() {
		this.replyCount = replyCount-1;
	}
	public void increaseViewCount() {
		this.viewCount = viewCount+1;
	}
	public void increaseBookmarkCount() {
		this.bookmarkCount = bookmarkCount+1;
	}
	public void decreaseBookmarkCount() {
		this.bookmarkCount = bookmarkCount-1;
	}
}
