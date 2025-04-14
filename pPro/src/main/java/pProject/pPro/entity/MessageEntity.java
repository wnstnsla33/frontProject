package pProject.pPro.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.Report.DTO.ReportStatusDTO;
import pProject.pPro.message.DTO.MessageType;
import pProject.pPro.message.DTO.SaveMessageDTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
	    @Index(name = "idx_message_sender_user_id", columnList = "sender_id"),
	    @Index(name = "idx_message_receiver_room_id", columnList = "receiver_id")
	})
public class MessageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long messageId;
	private String title;
	private String content;
	private LocalDateTime sendDate;
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private UserEntity sender;

	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private UserEntity receiver;
	
	private boolean isRead;
	@Enumerated(EnumType.STRING)
	private MessageType messagetype;
	public MessageEntity(SaveMessageDTO dto,UserEntity sender,UserEntity receiver) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.sendDate = LocalDateTime.now();
        this.isRead = false;
        this.sender=sender;
        this.receiver=receiver;
        this.messagetype= dto.getType();
    } 
	
	public MessageEntity(ReportStatusDTO dto,UserEntity sender,UserEntity receiver) {
        this.title = "신고 접수로 인해 주의가 필요합니다.";
        this.content = dto.getReason();
        this.sendDate = LocalDateTime.now();
        this.isRead = false;
        this.sender=sender;
        this.receiver=receiver;
        this.messagetype= MessageType.WARNING;
    } 
}
