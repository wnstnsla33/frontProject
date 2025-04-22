package pProject.pPro.replyLike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReplyLikeResponseDTO {
	private boolean isLiked;
	private int likeCount;
}
