package pProject.pPro.room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.ServiceUtils;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.RoomUser.DTO.RoomAddress;
import pProject.pPro.User.UserRepository;
import pProject.pPro.chat.ChatRepository;
import pProject.pPro.chat.DTO.MessageResponseDTO;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.SearchRoomDTO;
import pProject.pPro.room.excption.RoomErrorCode;
import pProject.pPro.room.excption.RoomException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

	private final RoomRepository roomRepository;
	private final HostUserRepository hostUserRepository;
	private final ChatRepository chatRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ServiceUtils utils;
	// 방 생성
	public RoomDTO createRoom(RoomDTO room, String email) {
		UserEntity user = utils.findUser(email);
		RoomEntity roomEntity = new RoomEntity(room);
		roomEntity.setRoomImg(utils.saveImage(room.getRoomSaveImg()));
		roomEntity.setCreateUser(user);
		roomEntity.setAddress(new RoomAddress(room.getSido(), room.getSigungu()));

		if (room.getSecretePassword() != null) {
			roomEntity.setSecretePassword(passwordEncoder.encode(room.getSecretePassword()));
		}

		RoomEntity saveRoom = roomRepository.save(roomEntity);
		hostUserRepository.save(new HostUserEntity(saveRoom, user));
		return new RoomDTO(saveRoom);
	}

	public String savedImage(MultipartFile file) {
		return utils.saveImage(file);
	}

	public RoomDTO findRoom(String roomId) {
		return new RoomDTO(roomRepository.findByIdForUpdate(roomId)
				.orElseThrow(() -> new RoomException(RoomErrorCode.ROOM_NOT_FOUND)));
	}

	public List<RoomDTO> roomList() {
		return roomRepository.findAll().stream().map(RoomDTO::new).toList();
	}

	public Page<RoomDTO> searchRooms(SearchRoomDTO dto) {
		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("roomCreatDate").descending());
		return roomRepository.searchRooms(dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu(), pageable)
				.map(RoomDTO::new);
	}

	public RoomDTO joinRoom(String roomId, String email) {
		boolean isAdmin = email.equals("admin@naver.com");

		Optional<HostUserEntity> findHostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (findHostUser.isPresent()) return new RoomDTO(findHostUser.get());

		RoomEntity room = utils.findRoom(roomId);

		if (!isAdmin && room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
			throw new RoomException(RoomErrorCode.FULL_CAPACITY);
		}

		UserEntity user = utils.findUser(email);
		hostUserRepository.save(new HostUserEntity(room, user));
		if (!isAdmin) room.setCurPaticipants(room.getCurPaticipants() + 1);

		return new RoomDTO(room);
	}

	public RoomDTO joinPwdRoom(String roomId, String email, String pwd) {
		RoomEntity room = utils.findRoom(roomId);
		if (!passwordEncoder.matches(pwd, room.getSecretePassword())) {
			throw new RoomException(RoomErrorCode.INVALID_PASSWORD);
		}

		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (hostUser.isPresent()) return new RoomDTO(hostUser.get(), true);

		if (room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
			throw new RoomException(RoomErrorCode.FULL_CAPACITY);
		}

		UserEntity user = utils.findUser(email);
		hostUserRepository.save(new HostUserEntity(room, user));
		room.setCurPaticipants(room.getCurPaticipants() + 1);

		return new RoomDTO(room, true);
	}

	public void deleteRoom(String roomId, String email) {
		RoomEntity room = utils.findRoom(roomId);
		if (!room.getCreateUser().getUserEmail().equals(email)) {
			Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
			if (hostUser.isPresent()) {
				hostUserRepository.delete(hostUser.get());
				room.setCurPaticipants(room.getCurPaticipants() - 1);
				return;
			}
			throw new RoomException(RoomErrorCode.NO_PERMISSION);
		}

		roomRepository.delete(room);
	}

	public RoomDTO updateRoom(RoomDTO room, String roomId, String email) {
		RoomEntity roomEntity = utils.findRoom(roomId);
		if (!roomEntity.getCreateUser().getUserEmail().equals(email)) {
			throw new RoomException(RoomErrorCode.NO_PERMISSION);
		}

		roomEntity.setRoomContent(room.getRoomContent());
		roomEntity.setCurPaticipants(room.getCurPaticipants());
		roomEntity.setRoomModifiedDate(LocalDateTime.now());
		roomEntity.setRoomTitle(room.getRoomTitle());
		roomEntity.setRoomType(room.getRoomType());

		return new RoomDTO(roomEntity);
	}

	public List<RoomDTO> getMyJoinRooms(String email) {
		List<HostUserEntity> hosts = hostUserRepository.findRoomsByUser(email);
		return hosts.stream().map(RoomDTO::new).toList();
	}

	public List<MessageResponseDTO> getChatList(String roomId, String email) {
		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (hostUser.isEmpty()) {
			throw new RoomException(RoomErrorCode.NOT_JOINED);
		}
		return chatRepository.chatListByRoom(roomId).stream()
				.map(MessageResponseDTO::new)
				.collect(Collectors.toList());
	}
}
