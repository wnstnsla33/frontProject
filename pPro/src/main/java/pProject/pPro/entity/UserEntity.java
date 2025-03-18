package pProject.pPro.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.User.DTO.SignupLoginDTO;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userId;
	//id
	@Column(name = "user_email")
	private String userEmail;
	//암호화됨
	private String userPassword;
	
	private String userNickName;
	//실명
	private String userName;
	
	private int userAge;
	@Enumerated(EnumType.STRING)
	private Grade userGrade;
	
	private String userBirthDay;
	
	private String userCreateDate;
	
	private String userSex;
	
	private String userInfo;
	
	private String Hint;
	
	@ColumnDefault("'https://i.namu.wiki/i/Bge3xnYd4kRe_IKbm2uqxlhQJij2SngwNssjpjaOyOqoRhQlNwLrR2ZiK-JWJ2b99RGcSxDaZ2UCI7fiv4IDDQ.webp'")
	private String userImg;

	@ColumnDefault("1")
	private int userExp;

	@ColumnDefault("1")
	private int userLevel;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("user-bookmark")
	private List<BookmarkEntity> bookmark = new ArrayList<BookmarkEntity>();

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<PostEntity> post = new ArrayList<PostEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("user-reply")
	private List<ReplyEntity> reply = new ArrayList<ReplyEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference("user-replyLike")
	private List<ReplyLikeEntity> replyLike = new ArrayList<ReplyLikeEntity>();
	
	
}
