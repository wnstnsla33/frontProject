package pProject.pPro.post.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private String content;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;

	private int viewCount;
	private Long userId;
	// bookmarkCount,likeCount는 DTO에서
	private int bookmarkCount;
	private boolean bookmarked = false;
	private boolean secreteKey;
	private String userNickName;
	private String userName;
	private String userImg;
	private boolean isPrivate;
	private int replyCount;
	public PostListDTO(PostEntity postEntity) {
		super();
		if (postEntity.getSecreteKey() != null) {
			this.postId = postEntity.getPostId();
			this.title = "비밀글";
			this.userNickName = postEntity.getUser().getUserNickName();
			this.viewCount = postEntity.getViewCount();
		} else {
			this.postId = postEntity.getPostId();
			this.userId= postEntity.getUser().getUserId();
			this.title = postEntity.getTitle();
			this.content = postEntity.getContent();
			this.createDate = postEntity.getCreateDate();
			this.modifiedDate = postEntity.getModifiedDate();
			this.viewCount = postEntity.getViewCount();
			this.bookmarkCount = postEntity.getBookmarkCount();
			this.userNickName = postEntity.getUser().getUserNickName();
			this.replyCount= postEntity.getReplyCount();
			this.userImg=postEntity.getUser().getUserImg();
		}

	}
	public PostListDTO(PostEntity postEntity,boolean isBookmarked) {
		super();
		if (postEntity.getSecreteKey() != null) {
			this.postId = postEntity.getPostId();
			this.title = "비밀글";
			this.userNickName = postEntity.getUser().getUserNickName();
			this.viewCount = postEntity.getViewCount();
			this.bookmarked = isBookmarked;
		} else {
			this.bookmarked = isBookmarked;
			this.userId= postEntity.getUser().getUserId();
			this.postId = postEntity.getPostId();
			this.title = postEntity.getTitle();
			this.content = postEntity.getContent();
			this.createDate = postEntity.getCreateDate();
			this.modifiedDate = postEntity.getModifiedDate();
			this.viewCount = postEntity.getViewCount();
			this.bookmarkCount = postEntity.getBookmarkCount();
			this.userNickName = postEntity.getUser().getUserNickName();
			this.replyCount= postEntity.getReplyCount();
			this.userImg=postEntity.getUser().getUserImg();
		}

	}
	public PostListDTO(PostEntity postEntity,boolean isBookmarked,boolean pass) {
		super();
		this.postId = postEntity.getPostId();
		this.userId= postEntity.getUser().getUserId();
		this.userName= postEntity.getUser().getUserName();
		this.userNickName=postEntity.getUser().getUserNickName();
		this.title = postEntity.getTitle();
		this.content = postEntity.getContent();
		this.createDate = postEntity.getCreateDate();
		this.modifiedDate = postEntity.getModifiedDate();
		this.viewCount = postEntity.getViewCount();
		this.bookmarkCount = postEntity.getBookmarkCount();
		this.userNickName = postEntity.getUser().getUserNickName();
		this.isPrivate = postEntity.getSecreteKey()!=null?true:false;		
		this.replyCount=postEntity.getReplyCount();
		this.bookmarked= isBookmarked;
		this.userImg=postEntity.getUser().getUserImg();
		}




//	public PostListDTO(BookmarkEntity bookmarkEntity) {
//		super();
//		if (bookmarkEntity.getPost().getSecreteKey() != null) {
//			this.postId = bookmarkEntity.getPost().getPostId();
//			this.userId= bookmarkEntity.getUser().getUserId();
//			this.title = "비밀글";
//			this.postId = bookmarkEntity.getPost().getPostId();
//			this.title = bookmarkEntity.getPost().getTitle();
//			this.content = bookmarkEntity.getPost().getContent();
//			this.createDate = bookmarkEntity.getPost().getCreateDate();
//			this.modifiedDate = bookmarkEntity.getPost().getModifiedDate();
//			this.viewCount = bookmarkEntity.getPost().getViewCount();
//			this.bookmarkCount = bookmarkEntity.getPost().getBookmarkCount();
//			this.userNickName = bookmarkEntity.getUser().getUserNickName();
//			this.replyCount= bookmarkEntity.getPost().getReplyCount();
//			this.bookmarked = true;
//		}
//	}
	public PostListDTO(BookmarkEntity bookmarkEntity,Boolean isBookmarked,boolean verify) {
		super();
		this.postId = bookmarkEntity.getPost().getPostId();
		this.title = bookmarkEntity.getPost().getTitle();
		this.userId= bookmarkEntity.getUser().getUserId();
		this.content = bookmarkEntity.getPost().getContent();
		this.createDate = bookmarkEntity.getPost().getCreateDate();
		this.modifiedDate = bookmarkEntity.getPost().getModifiedDate();
		this.viewCount = bookmarkEntity.getPost().getViewCount();
		this.bookmarkCount = bookmarkEntity.getPost().getBookmarkCount();
		this.userNickName = bookmarkEntity.getUser().getUserNickName();
		this.replyCount= bookmarkEntity.getPost().getReplyCount();
		this.bookmarked = true;
		this.isPrivate = bookmarkEntity.getPost().getSecreteKey()!=null?true:false;		
		this.userImg=bookmarkEntity.getUser().getUserImg();	
	}
	
	

}
