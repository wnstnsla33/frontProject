package pProject.pPro.message.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.MessageEntity;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseDTO {

    private Long messageId;
    private String title;
    private String content;
    private LocalDateTime sendDate;
    private Long senderId;
    private String senderName;
    private String receiverName;
    private boolean isRead;
    private MessageType messageType;

    public MessageResponseDTO(MessageEntity message) {
        this.messageId = message.getMessageId();
        this.title = message.getTitle();
        this.content = message.getContent();
        this.sendDate = message.getSendDate();
        this.senderName = message.getSender().getUserNickName();
        this.senderId = message.getSender().getUserId();
        this.receiverName = message.getReceiver().getUserNickName();
        this.isRead = message.isRead();
        this.messageType = message.getMessagetype();
    }
}
