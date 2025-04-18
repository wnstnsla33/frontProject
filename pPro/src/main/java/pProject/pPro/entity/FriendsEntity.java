package pProject.pPro.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.friends.DTO.RequestFriendsType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
	    @Index(name = "hostuser_reply_user_id", columnList = "my_id")
	})

public class FriendsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long friendsId;
	
	private LocalDateTime createdRelation;
	
	@Enumerated(EnumType.STRING)
    private RequestFriendsType type; // REQUESTED, ACCEPTED, BLOCKED 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_id")
    private UserEntity my;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private UserEntity friend;

	public FriendsEntity(UserEntity my, UserEntity friend) {
		super();
		this.createdRelation = LocalDateTime.now();
		this.type = RequestFriendsType.REQUEST;
		this.my = my;
		this.friend = friend;
	}
	
}
