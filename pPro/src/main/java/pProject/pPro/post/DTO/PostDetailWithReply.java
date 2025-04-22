package pProject.pPro.post.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pProject.pPro.reply.DTO.ReplyListDTO;

@Getter
@AllArgsConstructor
public class PostDetailWithReply {
	private PostListDTO post;
	private List<ReplyListDTO> replyList;
}
