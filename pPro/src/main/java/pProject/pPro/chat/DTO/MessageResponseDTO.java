package pProject.pPro.chat.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.ChatEntity;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseDTO {
	private String roomId;
    private String senderName;
    private String message;
    private LocalDateTime createTime;
    private String userImg;
	public MessageResponseDTO(ChatEntity chat) {
		super();
		this.roomId = chat.getRoom().getRoomId();
		this.senderName = chat.getUser().getUserName();
		this.message = chat.getMessage();
		this.createTime = chat.getCreateTime();
		this.userImg = chat.getUser().getUserImg();
	}
    
}
