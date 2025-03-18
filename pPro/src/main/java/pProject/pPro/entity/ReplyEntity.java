package pProject.pPro.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReplyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "reply_id")
	private Long replyId;

	private String content;
	
	private LocalDate createDate;
	private LocalDate modifiedDate;
	private int likeCount;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonManagedReference("user-reply")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
	
	
}