package pProject.pPro.room.chat;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.DTO.ChatMessageDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
	private final ChatRepository chatRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	public void saveMessage(ChatMessageDTO msg,String email) {
		RoomEntity room = roomRepository.findById(msg.getRoomId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
		UserEntity user =userRepository.findByEmail(email).orElseThrow( ()->new IllegalArgumentException(" 해당 아이디가 없습니다."));
		
		
		
		ChatEntity entity = new ChatEntity(msg.getMessage(), room,user);
		chatRepository.save(entity);

	}
}
