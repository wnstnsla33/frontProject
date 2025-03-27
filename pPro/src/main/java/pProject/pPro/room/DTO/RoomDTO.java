package pProject.pPro.room.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.RoomEntity;

@Getter
@Setter
@NoArgsConstructor
public class RoomDTO {
	private String roomId;
	private String roomTitle;
	private String roomType;
	private String roomContent;
	private int maxParticipants;
	private int curPaticipants;
	private String hostName;
	private LocalDateTime roomModifiedDate;
	private LocalDateTime roomCreatedAt;
	private String sido;
	private String sigungu;
	private String dong;

	public RoomDTO(RoomEntity room) {
        this.roomId = room.getRoomId();
        this.roomTitle = room.getRoomTitle();
        this.roomType = room.getRoomType();
        this.roomContent = room.getRoomContent();
        this.maxParticipants = room.getRoomMaxParticipants();
        this.curPaticipants = room.getCurPaticipants();
        this.roomCreatedAt = room.getRoomCreatDate();
        this.roomModifiedDate = room.getRoomModifiedDate();
        this.hostName = room.getCreateUser().getUserName();
    }
}
