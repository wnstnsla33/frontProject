package pProject.pPro.reply.DTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyRegDTO {
	private String content;
	private String email;
	private Long replyId;
	private Long parentReplyId;
}
