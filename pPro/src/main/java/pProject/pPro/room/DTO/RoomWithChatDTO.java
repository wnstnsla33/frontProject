package pProject.pPro.room.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pProject.pPro.chat.DTO.MessageResponseDTO;

@AllArgsConstructor
@Getter
public class RoomWithChatDTO {
	private List<MessageResponseDTO> messages;
	private RoomDTO roomData;
}
