package pProject.pPro.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PrivateChatEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long chatId;
	
	private String message;
	private LocalDateTime createTime;
//	@ManyToOne
//	@JoinColumn(name = "room_id")
//	private PrivateChatRoomEntity room;
	@ManyToOne
	@JoinColumn(name= "sender_id")
	private UserEntity sender;
	
	@ManyToOne
	@JoinColumn(name= "receiver_id")
	private UserEntity Receiver;
	
}
