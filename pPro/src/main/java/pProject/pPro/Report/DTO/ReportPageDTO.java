package pProject.pPro.Report.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class ReportPageDTO {
	private List<ReportResponseDTO> data;
	private int pageCount;
}
