package pProject.pPro.post.DTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import pProject.pPro.entity.PostEntity;
@Setter
@Getter
public class PostListDTO {
	private Long postId;
	
	private String title;
	private String titleImg;
	private String content;
	private LocalDate createDate;
	private LocalDate modifiedDate;
	
	private int viewCount;
	
	//bookmarkCount,likeCount는 DTO에서
	private int bookmarkCount;
	private String secreteKey; 
	private String userName;
	public PostListDTO(PostEntity postEntity) {
		super();
		this.postId = postEntity.getPostId();
		this.title = postEntity.getTitle();
		this.titleImg = postEntity.getTitleImg();
		this.content = postEntity.getContent();
		this.createDate = postEntity.getCreateDate();
		this.modifiedDate = postEntity.getModifiedDate();
		this.viewCount = postEntity.getViewCount();
		this.bookmarkCount = postEntity.getBookmarkCount();
		this.secreteKey = postEntity.getSecreteKey();
		this.userName = postEntity.getUser().getUserName();
	}
	
}
