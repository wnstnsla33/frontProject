package pProject.pPro.message.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public class MessageListDTO {

	private List<MessageResponseDTO> data;
	private int pageCount;
	private int unReadMsgCount;
}
