package pProject.pPro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import pProject.pPro.room.DTO.RoomDTO;

@Entity
@Getter
@Setter
public class RoomEntity {
	@Id
	private String roomId;
	private String roomType;
	private String roomTitle;
	private String roomContent;
	private int roomMaxParticipants;
	private int curPaticipants;
	private UserEntity createUser;
	private LocalDateTime roomCreatDate;
	private LocalDateTime roomModifiedDate;
	
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HostUserEntity> roomUsers = new ArrayList<>();
	
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
	}
}


