package pProject.pPro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pProject.pPro.User.DTO.SignupLoginDTO;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
	    indexes = {@Index(name = "idx_user_email", columnList = "user_email")  }
	)
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "user_id")
	private Long userId;
	// id
	@Column(name = "user_email",unique = true)
	private String userEmail;
	// 암호화됨
	private String userPassword;
	@Column(unique = true) // 👈 유니크 추가
	private String userNickName;
	// 실명
	private String userName;

	private int userAge;
	@Enumerated(EnumType.STRING)
	private Grade userGrade;

	private String userBirthDay;

	private String userCreateDate;

	private LocalDateTime recentLoginTime;

	private String userSex;

	private String userInfo;

	private int reportedCount;

	private LocalDateTime reportedDate;

	private String Hint;

	private int friendsCounts;
	
	@Embedded
	private Address address;

	private String userImg;

	@ColumnDefault("1")
	private int userExp;

	@ColumnDefault("1")
	private int userLevel;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("user-bookmark")
	private List<BookmarkEntity> bookmark = new ArrayList<BookmarkEntity>();

	@OneToMany(mappedBy = "user")
	@JsonBackReference("user-post")
	private List<PostEntity> post = new ArrayList<PostEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("user-reply")
	private List<ReplyEntity> reply = new ArrayList<ReplyEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("user-replyLike")
	private List<ReplyLikeEntity> replyLike = new ArrayList<ReplyLikeEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HostUserEntity> joinedRooms = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatEntity> chats = new ArrayList<>();

	@OneToMany(mappedBy = "createUser", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RoomEntity> createUsers = new ArrayList<>();

	@OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<ReportEntity> reportsByMe = new ArrayList<>();

	// 나를 신고한 리스트
	@OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<ReportEntity> reportsAgainstMe = new ArrayList<>();

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<MessageEntity> sentMessages = new ArrayList<>();

	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<MessageEntity> receivedMessages = new ArrayList<>();
	
	public UserEntity(SignupLoginDTO dto) {
		this.userEmail = dto.getEmail();
		this.userNickName = dto.getNickname();
		this.userName = dto.getRealName();
		this.userBirthDay = dto.getBirthDate().toString();
		this.reportedCount = 0;
		this.userAge = dto.getAge();
		this.userSex = dto.getGender();
		this.address = new Address(dto.getSido(), dto.getSigungu());
		this.userGrade = Grade.BRONZE;
		this.userCreateDate = LocalDateTime.now().toString();
		this.userLevel = 1;
		this.userExp = 0;
	}
	public void decreaseFriendsCounts() {
		this.friendsCounts = friendsCounts-1;
	}
	public void increaseFriendsCounts() {
		this.friendsCounts = friendsCounts+1;
	}
	public void increaseReportedCounts() {
		this.reportedCount = reportedCount+1;
	}
	public void decreaseReportedCounts() {
		this.reportedCount = reportedCount-1;
	}
	public void expUp() {
		
		this.userExp = userExp+ 20;

        if (userExp >= 100) {
            this.userLevel = userLevel+1;
            userExp=0;
            
            int level = userLevel;
            if (level >= 10) this.userGrade = Grade.VIP;
            else if (level >= 7) this.userGrade = Grade.GOLD;
            else if (level >= 4)this.userGrade = Grade.SILVER;
        } else {
            this.userExp=userExp;
        }
	}
}
