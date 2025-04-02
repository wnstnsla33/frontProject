package pProject.pPro.Admin.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.room.DTO.ChatMessageDTO;

@Getter
@Setter
@NoArgsConstructor
public class UserChatByAdmin {
	private String roomId;
	private String roomTitle;
	private LocalDateTime createdAt;
	private String msg;
	private Long chatId;
	public UserChatByAdmin(ChatEntity chat) {
		super();
		this.roomId = chat.getRoom().getRoomId();
		this.roomTitle = chat.getRoom().getRoomTitle();
		this.createdAt = chat.getCreateTime();
		this.msg = chat.getMessage();
		this.chatId = chat.getChatId();
	}
	
}
