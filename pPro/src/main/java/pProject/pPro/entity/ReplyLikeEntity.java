package pProject.pPro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ReplyLikeEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "replyLike_id")
    private Long replyLikeId;
	
	private LocalDateTime createDate;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
	private ReplyEntity reply;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	@JsonManagedReference("user-replyLike")
	private UserEntity user;

	public ReplyLikeEntity(ReplyEntity reply, UserEntity user) {
		super();
		this.reply = reply;
		this.user = user;
		this.createDate = LocalDateTime.now();
	}
	
}
