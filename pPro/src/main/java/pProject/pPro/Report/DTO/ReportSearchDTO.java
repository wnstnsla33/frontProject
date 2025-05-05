package pProject.pPro.Report.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportSearchDTO {
	private String keyword;
	private ReportStatus status;
	private int page;
}
