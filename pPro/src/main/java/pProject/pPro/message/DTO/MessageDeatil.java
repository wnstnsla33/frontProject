package pProject.pPro.message.DTO;

import java.time.LocalDateTime;

import pProject.pPro.entity.MessageEntity;

public class MessageDeatil {
	private Long messageId;
    private String title;
    private String content;
    private LocalDateTime sendDate;
    private String senderName;
    private String receiverName;
    private boolean isRead;
    private MessageType messageType;

    public MessageDeatil(MessageEntity message) {
        this.messageId = message.getMessageId();
        this.title = message.getTitle();
        this.content = message.getContent();
        this.sendDate = message.getSendDate();
        this.senderName = message.getSender().getUserNickName();
        this.receiverName = message.getReceiver().getUserNickName();
        this.isRead = message.isRead();
        this.messageType = message.getMessagetype();
    }
}
