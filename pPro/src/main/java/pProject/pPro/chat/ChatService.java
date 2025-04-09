package pProject.pPro.chat;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.ServiceUtils;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.chat.DTO.ChatMessageDTO;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.excption.RoomErrorCode;
import pProject.pPro.room.excption.RoomException;

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
		UserEntity user = utils.findUser(email);
		ChatEntity entity = new ChatEntity(msg.getMessage(), room, user);
		chatRepository.save(entity);
	}
	public boolean isHost(String  roomId,String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email); 
		if(hostUser.isPresent())return true;
		else return false;
	}

}
