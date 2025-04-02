package pProject.pPro.Admin.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailByAdmimDTO {
	private long userId;
	private String userEmail;
	//암호화됨
	private String userPassword;
	
	private String userNickName;
	//실명
	private String userName;
	
	private int userAge;
	
	private Grade userGrade;
	
	private String userBirthDay;
	
	private String userCreateDate;
	
	private String userSex;
	
	private String userInfo;
	
//	private String Hint;
	
	private String userImg;

	private int userExp;

	private int userLevel;
	private LocalDateTime recentLoginTime;
	public UserDetailByAdmimDTO(UserEntity userEntity) {
		super();
		this.userId = userEntity.getUserId();
		this.userEmail = userEntity.getUserEmail();
		this.userNickName = userEntity.getUserNickName();
		this.userName = userEntity.getUserName();
		this.userAge = userEntity.getUserAge();
		this.userGrade = userEntity.getUserGrade();
		this.userBirthDay = userEntity.getUserBirthDay();
		this.userCreateDate = userEntity.getUserCreateDate();
		this.userSex = userEntity.getUserSex();
		this.userInfo = userEntity.getUserInfo();
//		this.Hint = userEntity.getHint();
		this.userImg = userEntity.getUserImg();
		this.userExp = userEntity.getUserExp();
		this.userLevel = userEntity.getUserLevel();
		this.recentLoginTime = userEntity.getRecentLoginTime();
	}
}
