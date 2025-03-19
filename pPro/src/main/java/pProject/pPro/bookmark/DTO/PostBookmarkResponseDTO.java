package pProject.pPro.bookmark.DTO;

import lombok.Getter;
import lombok.Setter;
import pProject.pPro.entity.PostEntity;

@Getter
@Setter
public class PostBookmarkResponseDTO {
	private Long postId;
	private int postBookmarkCount;
	private boolean isBookmarked;
	public PostBookmarkResponseDTO(PostEntity post, boolean isBookmarked) {
		super();
		this.postId = post.getPostId();
		this.postBookmarkCount = post.getBookmarkCount();
		this.isBookmarked = isBookmarked;
	}
	
	
}
