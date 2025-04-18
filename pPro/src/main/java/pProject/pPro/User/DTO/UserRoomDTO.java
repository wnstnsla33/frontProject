package pProject.pPro.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRoomDTO {
	private String roomId;
	private String roomtitle;
	private String roomImg;
	private String roomType;
	private int curPaticipants;
	private int roomMaxParticipants;
	private boolean isPrivate;
	
}
