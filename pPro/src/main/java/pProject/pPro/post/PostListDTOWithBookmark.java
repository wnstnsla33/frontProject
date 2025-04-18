//package pProject.pPro.post;
//
//import java.time.LocalDateTime;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import pProject.pPro.entity.PostEntity;
//@Getter
//@Setter
//@NoArgsConstructor
////public class PostListDTOWithBookmark {
//	private Long postId;
//
//	private String title;
//	private String content;
//	private LocalDateTime createDate;
//	private LocalDateTime modifiedDate;
//
//	private int viewCount;
//
//	// bookmarkCount,likeCount는 DTO에서
//	private int bookmarkCount;
//	private boolean secreteKey;
//	private String userNickName;
//	private boolean bookmarked;
//	private boolean isPrivate;
//	private int replyCount;
//	
//	public PostListDTOWithBookmark(PostEntity postEntity) {
//		super();
//		if (postEntity.getSecreteKey() != null) {
//			this.postId = postEntity.getPostId();
//			this.title = "비밀글";
//			this.userNickName = postEntity.getUser().getUserNickName();
//			this.viewCount = postEntity.getViewCount();
//		} else {
//			this.postId = postEntity.getPostId();
//			this.title = postEntity.getTitle();
//			this.content = postEntity.getContent();
//			this.createDate = postEntity.getCreateDate();
//			this.modifiedDate = postEntity.getModifiedDate();
//			this.viewCount = postEntity.getViewCount();
//			this.bookmarkCount = postEntity.getBookmarkCount();
//			this.userNickName = postEntity.getUser().getUserNickName();
//			this.bookmarked = false;
//			this.replyCount= postEntity.getReplyCount();
//		}
//		}
//	
//	public PostListDTOWithBookmark(PostEntity postEntity,boolean isbookmarked) {
//	super();
//	if (postEntity.getSecreteKey() != null) {
//		this.postId = postEntity.getPostId();
//		this.title = "비밀글";
//		this.userNickName = postEntity.getUser().getUserNickName();
//		this.viewCount = postEntity.getViewCount();
//	} else {
//		this.postId = postEntity.getPostId();
//		this.title = postEntity.getTitle();
//		this.content = postEntity.getContent();
//		this.createDate = postEntity.getCreateDate();
//		this.modifiedDate = postEntity.getModifiedDate();
//		this.viewCount = postEntity.getViewCount();
//		this.bookmarkCount = postEntity.getBookmarkCount();
//		this.userNickName = postEntity.getUser().getUserNickName();
//		this.bookmarked = isbookmarked;
//		this.replyCount= postEntity.getReplyCount();
//	}
//	}
//}
