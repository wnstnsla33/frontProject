package pProject.pPro.friends.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pProject.pPro.entity.FriendsEntity;
import pProject.pPro.entity.UserEntity;

@NoArgsConstructor
@Getter
public class FriendsListDTO {
	private Long friendsId;
	private Long fId;
	private LocalDateTime recentLoginTime;
	private String friendsImg;
	private Long friendsUserId;
	private String friendsNickName;
	public FriendsListDTO(FriendsEntity friends, Long myId) {
		this.fId = friends.getFriendsId();

		UserEntity friend = friends.getMy().getUserId().equals(myId)
			? friends.getFriend()
			: friends.getMy();

		this.friendsId = friend.getUserId();
		this.recentLoginTime = friend.getRecentLoginTime();
		this.friendsImg = friend.getUserImg();
		this.friendsUserId = friend.getUserId();
		this.friendsNickName = friend.getUserNickName();
	}
	
}
