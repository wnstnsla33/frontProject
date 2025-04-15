package pProject.pPro.User.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
@Getter
@Setter
@NoArgsConstructor
public class UserDetailDTO {
	private long userId;
	private String userEmail;
	
	private String userNickName;
	
	private String userBirthDay;
	
	private String userInfo;
	
	private String userImg;
	
	private List<UserRoomDTO> rooms;
	private int userLevel;
	private LocalDateTime recentLoginTime;
	
	public UserDetailDTO(UserEntity user) {
		this.userId= user.getUserId();
		this.userEmail = user.getUserEmail();
		this.userNickName = user.getUserNickName();
		this.userBirthDay = user.getUserBirthDay();
		this.userInfo = user.getUserInfo();
		this.userImg=user.getUserImg();
		this.userLevel = user.getUserLevel();
		this.recentLoginTime = user.getRecentLoginTime();
		this.rooms = user.getJoinedRooms().stream()
				.map(join -> {
					RoomEntity room = join.getRoom();
					return new UserRoomDTO(
						room.getRoomId(),
						room.getRoomTitle(),
						room.getRoomImg(),
						room.getRoomType(),
						room.getCurPaticipants(),
						room.getRoomMaxParticipants(),
						room.getSecretePassword()!=null
					);
				})
				.collect(Collectors.toList());
	}
}
