package pProject.pPro.Report.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReportServiceDTO<T> {
	private boolean success;
	private T data;
	public ReportServiceDTO(boolean success) {
		super();
		this.success = success;
	}
	
}
