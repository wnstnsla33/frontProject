package pProject.pPro.Report.DTO;

import lombok.Getter;
import lombok.Setter;
import pProject.pPro.Report.ReportStatus;
import pProject.pPro.Report.ReportTargetType;
import pProject.pPro.entity.ReportEntity;
import pProject.pPro.entity.UserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportResponseDTO {

	private Long reportId;

	// 신고자 정보
	private Long reporterId;
	private String reporterEmail;
	private String reporterNickName;

	// 신고 대상 유저 정보 (null일 수 있음)
	private Long reportedUserId;
	private String reportedUserEmail;
	private String reportedUserNickName;

	// 신고 대상 정보
	private String targetId;
	private ReportTargetType targetType;
	private String reason;
	private int reportedCount;
	// 상태, 날짜
	private ReportStatus status;
	private String chatText;
	private LocalDateTime createdAt;
	private String parentId;
	public ReportResponseDTO(ReportEntity report) {
		this.reportId = report.getReportId();
		this.reporterId = report.getReporter().getUserId();
		this.reporterNickName = report.getReporter().getUserNickName();
		this.reportedCount = report.getReportedUser().getReportedCount();
		this.reportedUserId = report.getReportedUser().getUserId();
		this.reportedUserNickName = report.getReportedUser().getUserNickName();
		this.targetId = report.getTargetId();
		this.targetType = report.getTargetType();
		this.reason = report.getReason();
		this.status = report.getStatus();
		this.createdAt = report.getCreatedAt();
		this.chatText = report.getChatText();
		this.parentId = report.getParentId();
	}
}
