package pProject.pPro.room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.room.DTO.ChatMessageDTO;
import pProject.pPro.room.DTO.MessageResponseDTO;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomEnum;
import pProject.pPro.room.DTO.RoomServiceDTO;
import pProject.pPro.room.chat.ChatRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final HostUserRepository hostUserRepository;
	private final ChatRepository chatRepository;

	public RoomDTO createRoom(RoomDTO room, String email) {
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("계정이 없습니다"));
		RoomEntity roomEntity = new RoomEntity(room);
		roomEntity.setRoomId(UUID.randomUUID().toString());
		roomEntity.setRoomCreatDate(LocalDateTime.now());
		roomEntity.setRoomModifiedDate(LocalDateTime.now());
		roomEntity.setCurPaticipants(1);
		roomEntity.setCreateUser(user);
		RoomEntity saveRoom = roomRepository.save(roomEntity);
		HostUserEntity hostUserEntity = new HostUserEntity(saveRoom, user);
		hostUserRepository.save(hostUserEntity);
		return new RoomDTO(saveRoom);
	}

	public RoomDTO findRoom(String roomId) {
		return new RoomDTO(roomRepository.findByIdForUpdate(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId)));
	}

	public List<RoomDTO> roomList() {
		return roomRepository.findAll().stream().map(RoomDTO::new).collect(Collectors.toList());
	}

	public RoomServiceDTO updateRoom(RoomDTO room, String roomId,String email) {
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		if(roomEntity.getCreateUser().getUserEmail().equals(email)) return new RoomServiceDTO(RoomEnum.ROOM_FAIL,"수정 권한이 없습니다");
		roomEntity.setRoomContent(room.getRoomContent());
		roomEntity.setCurPaticipants(room.getCurPaticipants());
		roomEntity.setRoomModifiedDate(LocalDateTime.now());
		roomEntity.setRoomTitle(room.getRoomTitle());
		roomEntity.setRoomType(room.getRoomType());
		return new RoomServiceDTO(RoomEnum.ROOM_SUCCESS,roomEntity);
	}

	public RoomServiceDTO deleteRoom(String roomId, String email) {
		RoomEntity roomEntity = roomRepository.findByIdForUpdate(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		if (roomEntity.getCreateUser().getUserEmail().equals(email)) {
			try {
				roomRepository.deleteById(roomId);
				return new RoomServiceDTO(RoomEnum.ROOM_SUCCESS, "방 삭제되었습니다.");
			} catch (Exception e) {
				log.error("방 삭제중 예외 발생", e);
				return new RoomServiceDTO(RoomEnum.ROOM_FAIL, "방삭제 중 오류 발생됨");
				// TODO: handle exception
			}
		} else {
			Optional<HostUserEntity> hostUserEntity = hostUserRepository.findLoginEmail(roomId, email);
			if(hostUserEntity.isPresent()) {
				hostUserRepository.delete(hostUserEntity.get());
				roomEntity.setCurPaticipants(roomEntity.getCurPaticipants()-1);
				return new RoomServiceDTO(RoomEnum.ROOM_SUCCESS,"정상 탈퇴되었습니다");
			}
			else return new RoomServiceDTO(RoomEnum.ROOM_FAIL,"해당 방의 회원이 아닙니다");
		}

	}

	public RoomServiceDTO joinRoom(String roomId, String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("계정이 없습니다"));
		if (hostUser.isPresent())
			return new RoomServiceDTO(RoomEnum.ROOM_SUCCESS, new RoomDTO(roomEntity));
		HostUserEntity hostuserEntity = new HostUserEntity(roomEntity, user);
		hostuserEntity.setJoinedTime(LocalDateTime.now());
		// 꽉참
		if (roomEntity.getRoomMaxParticipants() == roomEntity.getCurPaticipants())
			return new RoomServiceDTO(RoomEnum.MAX_PARTICIPANTS, "인원이 꽉찼습니다.");
		try {
			hostUserRepository.save(hostuserEntity);
			roomEntity.setCurPaticipants(roomEntity.getCurPaticipants() + 1);
			return new RoomServiceDTO(RoomEnum.ROOM_SUCCESS, new RoomDTO(roomEntity));
		} catch (Exception e) {
			// TODO: handle exception
			log.error("방 참가 중 예외 발생", e);
			return new RoomServiceDTO(RoomEnum.ROOM_FAIL, "방 입장중 오류 발생됨");
		}
	}

	public RoomServiceDTO exitRoom(String roomId, String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (!hostUser.isPresent())
			return new RoomServiceDTO(RoomEnum.NONE_EXIST, "해당 방에 계정이 없습니다.");
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		try {
			hostUserRepository.delete(hostUser.get());
			roomEntity.setCurPaticipants(roomEntity.getCurPaticipants() - 1);
			return new RoomServiceDTO(RoomEnum.ROOM_SUCCESS, "방 정상 탈퇴 완료");
		} catch (Exception e) {
			// TODO: handle exception
			log.error("방 탈퇴 중 예외 발생", e);
			return new RoomServiceDTO(RoomEnum.ROOM_FAIL, "방 탈퇴 중 오류 발생");
		}
	}

	// 방의 메세지 가져오는(해당 계정이 방안에있는지 확인 후)
	public RoomServiceDTO getChatList(String roomId, String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (!hostUser.isPresent())
			return new RoomServiceDTO<String>(RoomEnum.NONE_EXIST, "방에 들어와 있지 않습니다.");
		List<MessageResponseDTO> chatList = chatRepository.chatListByRoom(roomId).stream().map(MessageResponseDTO::new)
				.collect(Collectors.toList());
		return new RoomServiceDTO<List<MessageResponseDTO>>(RoomEnum.ROOM_SUCCESS, chatList);
	}
}
