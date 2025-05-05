package pProject.pPro.Report.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor

public class ReportStatusDTO {
	@NotNull
	private ReportStatus status;
	private String reason;
}
