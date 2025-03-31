package pProject.pPro.room.DTO;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;

@Getter
@Setter
@NoArgsConstructor
public class RoomDTO {
	private String roomId;
	private String roomTitle;
	private String roomType;
	private MultipartFile roomSaveImg;
	private String roomImg;
	private String roomContent;
	private int maxParticipants;
	private int curPaticipants;
	private String hostName;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime meetingTime;
	private LocalDateTime roomModifiedDate;
	private LocalDateTime roomCreatedAt;
	private String sido;
	private String sigungu;
	private List<RoomMemberDTO> roomMembers;
	
	public RoomDTO(RoomEntity room) {
        this.roomId = room.getRoomId();
        this.roomTitle = room.getRoomTitle();
        this.roomType = room.getRoomType();
        this.roomContent = room.getRoomContent();
        this.maxParticipants = room.getRoomMaxParticipants();
        this.meetingTime=room.getMeetingTime();
        this.curPaticipants = room.getCurPaticipants();
        this.roomCreatedAt = room.getRoomCreatDate();
        this.roomModifiedDate = room.getRoomModifiedDate();
        this.hostName = room.getCreateUser().getUserName();
        this.roomImg =room.getRoomImg();
        this.roomMembers = room.getHostUsers().stream().map(hostUser ->new RoomMemberDTO( hostUser.getUser().getUserImg(),
        	    hostUser.getUser().getUserNickName(),hostUser.getUser().getUserInfo())).toList();
    }
	public RoomDTO(HostUserEntity hostUserEntity) {
        this.roomId = hostUserEntity.getRoom().getRoomId();
        this.roomTitle = hostUserEntity.getRoom().getRoomTitle();
        this.roomType = hostUserEntity.getRoom().getRoomType();
        this.roomContent = hostUserEntity.getRoom().getRoomContent();
        this.maxParticipants = hostUserEntity.getRoom().getRoomMaxParticipants();
        this.meetingTime=hostUserEntity.getRoom().getMeetingTime();
        this.curPaticipants = hostUserEntity.getRoom().getCurPaticipants();
        this.roomCreatedAt = hostUserEntity.getRoom().getRoomCreatDate();
        this.roomModifiedDate = hostUserEntity.getRoom().getRoomModifiedDate();
        this.hostName = hostUserEntity.getRoom().getCreateUser().getUserName();
        this.roomImg =hostUserEntity.getRoom().getRoomImg();
    }
}
