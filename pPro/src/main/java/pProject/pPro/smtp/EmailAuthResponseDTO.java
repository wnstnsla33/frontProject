package pProject.pPro.smtp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthResponseDTO {
	private boolean success;
	private String responseMessage;
	
}
