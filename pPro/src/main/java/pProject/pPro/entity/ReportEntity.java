package pProject.pPro.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import pProject.pPro.Report.ReportStatus;
import pProject.pPro.Report.ReportTargetType;
import pProject.pPro.Report.DTO.CreateReportDTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReportEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private UserEntity reporter;

	private String chatText;
	// 나를 신고한 사람 (신고 대상자, 선택적)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_user_id")
	private UserEntity reportedUser;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportTargetType targetType;
	
	@Column(nullable = false)
	private String reason;
	
	private String parentId;
	
	private String targetId;
	@Enumerated(EnumType.STRING)
	private ReportStatus status;
	
	private LocalDateTime createdAt;

	public ReportEntity(CreateReportDTO dto,UserEntity reporter,UserEntity reportedUser) {
		super();
		this.targetId= dto.getTargetId();
		this.createdAt = LocalDateTime.now();
		this.targetType = dto.getTargetType();
		this.reason = dto.getReason();
		this.status = ReportStatus.PENDING;
		this.reporter = reporter;
		this.reportedUser = reportedUser;
	}
	
	
}
