package pProject.pPro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.RoomUser.DTO.RoomAddress;
import pProject.pPro.room.DTO.RoomDTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RoomEntity {
	@Id
	@Column(name = "room_id")
	private String roomId;
	private String roomTitle;
	private String roomContent;
	private int roomMaxParticipants;
	private int curPaticipants;
	private String roomImg;
	private LocalDateTime meetingTime;
	private LocalDateTime roomCreatDate;
	private LocalDateTime roomModifiedDate;
	private String secretePassword;
	@Embedded
	private RoomAddress address;
	
	
	private String roomType;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity createUser;
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HostUserEntity> hostUsers = new ArrayList<HostUserEntity>();
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ChatEntity> chats = new ArrayList<ChatEntity>();
	
	public RoomEntity(RoomDTO roomDTO) {
		super();
		this.roomType = roomDTO.getRoomType();
		this.roomTitle = roomDTO.getRoomTitle();
		this.roomContent = roomDTO.getRoomContent();
		this.roomMaxParticipants = roomDTO.getMaxParticipants();
		this.roomCreatDate = roomDTO.getRoomCreatedAt();
		this.roomModifiedDate = roomDTO.getRoomModifiedDate();
		this.curPaticipants = roomDTO.getCurPaticipants(); 
		this.meetingTime= roomDTO.getMeetingTime();
		this.setRoomId(UUID.randomUUID().toString());
		this.setRoomCreatDate(LocalDateTime.now());
		this.setRoomModifiedDate(LocalDateTime.now());
		this.setCurPaticipants(1);
	}
}


