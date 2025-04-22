package pProject.pPro.room.DTO;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RoomListDTO {
	private List<RoomDTO> data;
	private int totalPages;
	public RoomListDTO(Page<RoomDTO> data) {
		super();
		this.data = data.getContent();
		this.totalPages = data.getTotalPages();
	}
	
}
