package pProject.pPro.chat;

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
		log.info("********** saveMessage() 호출 - roomId: {}, email: {}, message: '{}' **********", msg.getRoomId(), email, msg.getMessage());

		RoomEntity room = utils.findRoom(msg.getRoomId());
		UserEntity user = utils.findUser(email);
		ChatEntity entity = new ChatEntity(msg.getMessage(), room, user);
		chatRepository.save(entity);

		log.info("********** 채팅 저장 완료 - user: {}, room: {} **********", user.getUserName(), room.getRoomTitle());
	}

	public boolean isHost(String roomId, String email) {
		log.info("********** isHost() 호출 - roomId: {}, email: {} **********", roomId, email);
		UserEntity user = utils.findUser(email);
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginId(roomId, user.getUserId()); 
		boolean result = hostUser.isPresent();

		return result;
	}
}
