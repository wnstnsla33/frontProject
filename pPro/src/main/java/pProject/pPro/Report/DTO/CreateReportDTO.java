package pProject.pPro.Report.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.Report.ReportTargetType;
@Getter
@Setter
@NoArgsConstructor

public class CreateReportDTO {
		@NotNull
	 	private String targetId;

	    // 신고 대상 타입 (예: POST, COMMENT, CHAT, USER)
		@NotNull
	    private ReportTargetType targetType;

	    // 신고 대상 유저 ID (선택적)
	    
	    
	    private Long reportedUserId;

	    // 신고 사유
	    @NotNull(message = "신고 사유를 적어주십시오")
	    private String reason;
}
