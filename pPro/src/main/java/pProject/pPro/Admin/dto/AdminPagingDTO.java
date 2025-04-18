package pProject.pPro.Admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPagingDTO {
	private List< AdminUserDTO> data;
	private int pages;
}
