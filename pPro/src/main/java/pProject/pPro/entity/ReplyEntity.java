package pProject.pPro.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.reply.DTO.ReplyRegDTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
	    @Index(name = "idx_reply_user_id", columnList = "user_id")
	})
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

	@ManyToOne
	@JoinColumn(name = "parent_id",nullable = true)
	private ReplyEntity parent; // 부모 댓글 (없으면 null)

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReplyEntity> replies = new ArrayList<ReplyEntity>();

	public ReplyEntity(PostEntity post, UserEntity user, ReplyRegDTO replyRegDTO,ReplyEntity parent) {
		super();
		this.content = replyRegDTO.getContent();
		this.createDate = LocalDate.now();
		this.modifiedDate = LocalDate.now();
		this.likeCount = 0;
		this.post = post;
		this.user = user;
		this.parent=parent;
	}

}