package pProject.pPro.Report.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pProject.pPro.Report.ReportStatus;
import pProject.pPro.Report.ReportTargetType;
@Getter
@NoArgsConstructor

public class ReportStatusDTO {
	@NotNull
	private ReportStatus status;
	private String reason;
}
