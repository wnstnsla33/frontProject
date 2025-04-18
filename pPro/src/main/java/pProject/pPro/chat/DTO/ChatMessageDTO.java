package pProject.pPro.chat.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessageDTO {
	public enum MessageType {
        ENTER, TALK, QUIT,BANNED
    }

    private MessageType type;
    private String roomId;
    private String senderName;
    private String message;
    private String userImg;
    private Long userId;
}
