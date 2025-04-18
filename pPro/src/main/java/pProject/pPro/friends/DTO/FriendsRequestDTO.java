package pProject.pPro.friends.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pProject.pPro.entity.FriendsEntity;

@Getter
@NoArgsConstructor
public class FriendsRequestDTO {
	private Long friendsId;
	private Long fId;
	private LocalDateTime recentLoginTime;
	private String friendsImg;
	private Long friendsUserId;
	private String friendsNickName;

	public FriendsRequestDTO(FriendsEntity friends) {
		super();
		this.friendsId = friends.getMy().getUserId();
		this.friendsImg = friends.getMy().getUserImg();
		this.friendsUserId = friends.getMy().getUserId();
		this.friendsNickName = friends.getMy().getUserNickName();
		this.fId = friends.getFriendsId();
	}
}
