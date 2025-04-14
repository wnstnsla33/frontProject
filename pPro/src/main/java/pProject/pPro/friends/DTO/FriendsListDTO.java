package pProject.pPro.friends.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pProject.pPro.entity.FriendsEntity;

@NoArgsConstructor
@Getter
public class FriendsListDTO {
	private Long friendsId;
	private Long fId;
	private LocalDateTime recentLoginTime;
	private String friendsImg;
	private Long friendsUserId;
	private String friendsNickName;
	public FriendsListDTO(FriendsEntity friends) {
		super();
		this.friendsId = friends.getFriend().getUserId();
		this.recentLoginTime = friends.getFriend().getRecentLoginTime();
		this.friendsImg = friends.getFriend().getUserImg();
		this.friendsUserId = friends.getFriend().getUserId();
		this.friendsNickName = friends.getFriend().getUserNickName();
		this.fId = friends.getFriendsId();
	}
	
}
