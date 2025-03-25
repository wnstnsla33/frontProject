package pProject.pPro.room.chat;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.DTO.ChatMessageDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
	private final ChatRepository chatRepository;
	private final RoomRepository roomRepository;

	public void saveMessage(ChatMessageDTO msg) {
		RoomEntity room = roomRepository.findById(msg.getRoomId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

		ChatEntity entity = new ChatEntity(msg.getMessage(), room);
		chatRepository.save(entity);

	}
}
