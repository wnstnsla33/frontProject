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
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomEnum;
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
		return new RoomDTO(saveRoom);
	}

	public RoomDTO findRoom(String roomId) {
		return new RoomDTO(roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId)));
	}

	public List<RoomDTO> roomList() {
		return roomRepository.findAll().stream().map(RoomDTO::new).collect(Collectors.toList());
	}

	public RoomDTO updateRoom(RoomDTO room, String roomId) {
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		roomEntity.setRoomContent(room.getRoomContent());
		roomEntity.setCurPaticipants(room.getCurPaticipants());
		roomEntity.setRoomModifiedDate(LocalDateTime.now());
		roomEntity.setRoomTitle(room.getRoomTitle());
		roomEntity.setRoomType(room.getRoomType());
		return new RoomDTO(roomEntity);
	}

	public RoomEnum deleteRoom(String roomId, String email) {
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		if (roomEntity.getCreateUser().getUserName().equals(email)) {
			try {
				
			roomRepository.deleteById(roomId);
			return RoomEnum.ROOM_SUCCESS;
			}catch (Exception e) {
				log.error("방 삭제중 예외 발생", e);
				return RoomEnum.ROOM_FAIL;
				// TODO: handle exception
			}
		} else
			return RoomEnum.HOST_ONLY;
	}

	public RoomEnum joinRoom(String roomId, String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (hostUser.isPresent())
			return RoomEnum.ALREADY_HOST;
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("계정이 없습니다"));
		HostUserEntity hostuserEntity = new HostUserEntity(roomEntity, user);
		hostuserEntity.setJoinedTime(LocalDateTime.now());
		// 꽉참
		if (roomEntity.getRoomMaxParticipants() == roomEntity.getCurPaticipants())
			return RoomEnum.MAX_PARTICIPANTS;
		try {
			hostUserRepository.save(hostuserEntity);
			roomEntity.setCurPaticipants(roomEntity.getCurPaticipants() + 1);
			return RoomEnum.ROOM_SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			log.error("방 참가 중 예외 발생", e);
			return RoomEnum.ROOM_FAIL;
		}
	}

	public RoomEnum exitRoom(String roomId, String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (!hostUser.isPresent())
			return RoomEnum.ALREADY_EXIT;
		RoomEntity roomEntity = roomRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 방이 없습니다: " + roomId));
		try {
			hostUserRepository.delete(hostUser.get());
			roomEntity.setCurPaticipants(roomEntity.getCurPaticipants() - 1);
			return RoomEnum.ROOM_SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			log.error("방 탈퇴 중 예외 발생", e);
			return RoomEnum.ROOM_FAIL;
		}
	}
	public List<ChatMessageDTO> getChatList(String roomId,String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if(!hostUser.isPresent())
	}
}
