package pProject.pPro.room.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import pProject.pPro.entity.RoomEntity;
@Getter
public class RoomEditInfo {
	private String title;
	private String type;
	private String content;
	private int maxParticipants;
	private LocalDateTime meetingTime;
	public RoomEditInfo(RoomEntity room) {
		super();
		this.title = room.getRoomTitle();
		this.type = room.getRoomType();
		this.content = room.getRoomContent();
		this.maxParticipants = room.getRoomMaxParticipants();
		this.meetingTime  = room.getMeetingTime();
	}
	
	
	
}
