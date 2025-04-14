package pProject.pPro.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import pProject.pPro.RoomUser.DTO.HostUserStatus;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
	    @Index(name = "hostuser_reply_user_id", columnList = "user_id")
	})
public class HostUserEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    private LocalDateTime joinedTime;
    @Enumerated(EnumType.STRING)
    private HostUserStatus status;
	public HostUserEntity(RoomEntity room, UserEntity user) {
		super();
		this.joinedTime = LocalDateTime.now();
		this.status = HostUserStatus.JOINED;
		this.room = room;
		this.user = user;
	}
    
}
