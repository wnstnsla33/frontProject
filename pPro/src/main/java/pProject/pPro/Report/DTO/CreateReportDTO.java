package pProject.pPro.Report.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateReportDTO {
		@NotNull
	 	private String targetId;

		@NotNull
	    private ReportTargetType targetType;

	    // 신고 대상 유저 ID (선택적)
	    
	    
	    private Long reportedUserId;

	    // 신고 사유
	    @NotNull(message = "신고 사유를 적어주십시오")
	    private String reason;
}
