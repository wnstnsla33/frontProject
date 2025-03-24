package pProject.pPro.post.DTO;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;

@Setter
@Getter
@NoArgsConstructor
public class PostListDTO {
	private Long postId;

	private String title;
	private String titleImg;
	private String content;
	private LocalDate createDate;
	private LocalDate modifiedDate;

	private int viewCount;

	// bookmarkCount,likeCount는 DTO에서
	private int bookmarkCount;
	private boolean secreteKey;
	private String userName;
	private boolean bookmarked;

	public PostListDTO(PostEntity postEntity) {
		super();
		if (postEntity.getSecreteKey() != null) {
			this.postId = postEntity.getPostId();
			this.title = "비밀글";
			this.titleImg = "https://cafe24.poxo.com/ec01/sl63amg/MkCj60lH04MGF8dcvBKuW0eybFCsgGe/j2Yh65v5oJMmicHUSYBdNgf/Ld5X9bA8CqEQzGcDJdDxdOlUDXXqig==/_/web/upload/icon_202102212329010800.png";
		} else {
			this.postId = postEntity.getPostId();
			this.title = postEntity.getTitle();
			this.titleImg = postEntity.getTitleImg();
			this.content = postEntity.getContent();
			this.createDate = postEntity.getCreateDate();
			this.modifiedDate = postEntity.getModifiedDate();
			this.viewCount = postEntity.getViewCount();
			this.bookmarkCount = postEntity.getBookmarkCount();
			this.userName = postEntity.getUser().getUserName();
			this.bookmarked = false;
		}

	}
	
	public PostListDTO(PostEntity postEntity,int pass) {
		super();
		this.postId = postEntity.getPostId();
		this.title = postEntity.getTitle();
		this.titleImg = postEntity.getTitleImg();
		this.content = postEntity.getContent();
		this.createDate = postEntity.getCreateDate();
		this.modifiedDate = postEntity.getModifiedDate();
		this.viewCount = postEntity.getViewCount();
		this.bookmarkCount = postEntity.getBookmarkCount();
		this.userName = postEntity.getUser().getUserName();
		this.bookmarked = false;
		}



	public boolean getBookmarked() {
		return bookmarked;
	}

	public PostListDTO(BookmarkEntity bookmarkEntity) {
		super();
		if (bookmarkEntity.getPost().getSecreteKey() != null) {
			this.postId = bookmarkEntity.getPost().getPostId();
			this.title = "비밀글";
			this.titleImg = "https://cafe24.poxo.com/ec01/sl63amg/MkCj60lH04MGF8dcvBKuW0eybFCsgGe/j2Yh65v5oJMmicHUSYBdNgf/Ld5X9bA8CqEQzGcDJdDxdOlUDXXqig==/_/web/upload/icon_202102212329010800.png";
		} else {
			this.postId = bookmarkEntity.getPost().getPostId();
			this.title = bookmarkEntity.getPost().getTitle();
			this.titleImg = bookmarkEntity.getPost().getTitleImg();
			this.content = bookmarkEntity.getPost().getContent();
			this.createDate = bookmarkEntity.getPost().getCreateDate();
			this.modifiedDate = bookmarkEntity.getPost().getModifiedDate();
			this.viewCount = bookmarkEntity.getPost().getViewCount();
			this.bookmarkCount = bookmarkEntity.getPost().getBookmarkCount();
			this.userName = bookmarkEntity.getUser().getUserName();
			this.bookmarked = false;
		}
	}
	public PostListDTO(BookmarkEntity bookmarkEntity,int pass) {
		super();
		this.postId = bookmarkEntity.getPost().getPostId();
		this.title = bookmarkEntity.getPost().getTitle();
		this.titleImg = bookmarkEntity.getPost().getTitleImg();
		this.content = bookmarkEntity.getPost().getContent();
		this.createDate = bookmarkEntity.getPost().getCreateDate();
		this.modifiedDate = bookmarkEntity.getPost().getModifiedDate();
		this.viewCount = bookmarkEntity.getPost().getViewCount();
		this.bookmarkCount = bookmarkEntity.getPost().getBookmarkCount();
		this.userName = bookmarkEntity.getUser().getUserName();
		this.bookmarked = false;
		}

}
