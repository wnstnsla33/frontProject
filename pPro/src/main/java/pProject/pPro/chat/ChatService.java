package pProject.pPro.chat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.User.UserRepository;
import pProject.pPro.chat.DTO.ChatMessageDTO;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.room.RoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
	private final ChatRepository chatRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final HostUserRepository hostUserRepository;
	private final ServiceUtils utils;

	public void saveMessage(ChatMessageDTO msg, String email) {

		RoomEntity room = utils.findRoom(msg.getRoomId());
		room.setRecentChat(LocalDateTime.now());
		UserEntity user = utils.findUser(email);
		ChatEntity entity = new ChatEntity(msg.getMessage(), room, user);
		chatRepository.save(entity);
	}

	public boolean isHost(String roomId, String email) {
		UserEntity user = utils.findUser(email);
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginId(roomId, user.getUserId()); 
		boolean result = hostUser.isPresent();

		return result;
	}
}
