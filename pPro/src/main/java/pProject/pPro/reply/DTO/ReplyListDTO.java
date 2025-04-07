package pProject.pPro.reply.DTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import pProject.pPro.entity.ReplyEntity;

@Getter
@Setter

public class ReplyListDTO {
	private Long userId;
	private Long replyId;
	private String content;
	private String userNickname;
	private LocalDate createDate;
	private LocalDate modifiedDate;
	private Long postId;
	private List<ReplyListDTO> replys;
	public ReplyListDTO(ReplyEntity reply) {
		super();
		this.userId = reply.getUser().getUserId();
		this.replyId = reply.getReplyId();
		this.content = reply.getContent();
		this.userNickname = reply.getUser().getUserNickName();
		this.createDate = reply.getCreateDate();
		this.modifiedDate = reply.getModifiedDate();
		this.postId = reply.getPost().getPostId();
		this.replys = reply.getReplies().stream()
                .map(ReplyListDTO::new)
                .collect(Collectors.toList());
	}
	
	
	
}
