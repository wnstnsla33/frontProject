package pProject.pPro.room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
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
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.RoomUser.DTO.HostUserStatus;
import pProject.pPro.RoomUser.DTO.RoomAddress;
import pProject.pPro.User.UserRepository;
import pProject.pPro.chat.ChatRepository;
import pProject.pPro.chat.DTO.MessageResponseDTO;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomWithChatDTO;
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

	public RoomDTO createRoom(RoomDTO room, String email) {
		log.info("********** createRoom() 호출 - email: {}, roomTitle: {} **********", email, room.getRoomTitle());

		UserEntity user = utils.findUser(email);
		RoomEntity roomEntity = new RoomEntity(room);
		roomEntity.setRoomImg(utils.saveImage(room.getRoomSaveImg()));
		roomEntity.setCreateUser(user);
		roomEntity.setAddress(new RoomAddress(room.getSido(), room.getSigungu()));

		if (room.getSecretePassword() != null) {
			log.info("🔐 비밀방 설정됨 - 비밀번호 암호화 처리");
			roomEntity.setSecretePassword(passwordEncoder.encode(room.getSecretePassword()));
		}

		RoomEntity saveRoom = roomRepository.save(roomEntity);
		try {
			hostUserRepository.save(new HostUserEntity(saveRoom, user));
		} catch (DataIntegrityViolationException e) {
			log.warn("🚫 유니크 제약 위반: 같은 유저가 같은 제목의 방 생성 시도");
			throw new RoomException(RoomErrorCode.DUPLICATE_ROOM);
		}
		log.info("✅ 방 생성 완료 - roomId: {}", saveRoom.getRoomId());
		return new RoomDTO(saveRoom);
	}

	public String savedImage(MultipartFile file) {
		log.info("********** savedImage() 호출 - 파일명: {} **********", file.getOriginalFilename());
		return utils.saveImage(file);
	}

	public RoomDTO findRoom(String roomId) {
		log.info("********** findRoom() 호출 - roomId: {} **********", roomId);
		return new RoomDTO(roomRepository.findByIdForUpdate(roomId).orElseThrow(() -> {
			log.warn("🚫 방 조회 실패 - 존재하지 않음: {}", roomId);
			return new RoomException(RoomErrorCode.ROOM_NOT_FOUND);
		}));
	}

	public List<RoomDTO> roomList() {
		log.info("********** roomList() 호출 **********");
		return roomRepository.findAll().stream().map(RoomDTO::new).toList();
	}

	public Page<RoomDTO> searchRooms(SearchRoomDTO dto) {
		log.info("********** searchRooms() 호출 - title: {}, type: {}, sido: {}, sigungu: {} **********", dto.getTitle(),
				dto.getRoomType(), dto.getSido(), dto.getSigungu());

		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("roomCreatDate").descending());
		return roomRepository.searchRooms(dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu(), pageable)
				.map(RoomDTO::new);
	}

	public Page<RoomDTO> searchRooms(SearchRoomDTO dto, String email) {
		UserEntity user = utils.findUser(email);
		dto.setSido(user.getAddress().getSido());
		dto.setSigungu(user.getAddress().getSigungu());
		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("roomCreatDate").descending());
		return roomRepository.searchRooms(dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu(), pageable)
				.map(RoomDTO::new);
	}

	public RoomWithChatDTO joinRoom(String roomId, String email) {
		log.info("🔔 joinRoom() 호출 - roomId: {}, email: {}", roomId, email);

		boolean isAdmin = email.equals("admin@naver.com");
		UserEntity user = utils.findUser(email);
		RoomEntity room = roomRepository.fetchRoomWithHostUsers(roomId)
				.orElseThrow(() -> new RoomException(RoomErrorCode.INVALID_ID));

		Optional<HostUserEntity> optionalHostUser = hostUserRepository.findLoginId(roomId, user.getUserId());

		HostUserEntity hostUser;

		if (optionalHostUser.isPresent()) {
			hostUser = optionalHostUser.get();
			switch (hostUser.getStatus()) {
			case BANNED -> throw new RoomException(RoomErrorCode.ISBANNED);
			case LEFT -> {
				if (!isAdmin && room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
					throw new RoomException(RoomErrorCode.FULL_CAPACITY);
				}
				hostUser.setStatus(HostUserStatus.JOINED);
				room.getHostUsers().add(hostUser);
				room.setCurPaticipants(room.getCurPaticipants() + 1);
			}
			case JOINED -> {
			}
			}
		} else {
			if (!isAdmin && room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
				throw new RoomException(RoomErrorCode.FULL_CAPACITY);
			}
			hostUser = hostUserRepository.save(new HostUserEntity(room, user));
			room.getHostUsers().add(hostUser); // ✅ 새로운 유저는 당연히 넣어줘야 함
			if (!isAdmin)
				room.setCurPaticipants(room.getCurPaticipants() + 1);
		}

		List<MessageResponseDTO> messages = chatRepository.chatListByRoom(roomId).stream().map(MessageResponseDTO::new)
				.toList();

		return new RoomWithChatDTO(messages, new RoomDTO(room));
	}

	public RoomWithChatDTO joinPwdRoom(String roomId, String email, String pwd) {
		UserEntity user = utils.findUser(email);
		RoomEntity room = roomRepository.fetchRoomWithHostUsers(roomId)
				.orElseThrow(() -> new RoomException(RoomErrorCode.INVALID_ID));

		if (!passwordEncoder.matches(pwd, room.getSecretePassword())) {
			throw new RoomException(RoomErrorCode.INVALID_PASSWORD);
		}

		Optional<HostUserEntity> optionalHostUser = hostUserRepository.findLoginId(roomId, user.getUserId());
		HostUserEntity hostUser;
		if (optionalHostUser.isPresent()) {
			hostUser = optionalHostUser.get();

			switch (hostUser.getStatus()) {
			case BANNED -> throw new RoomException(RoomErrorCode.ISBANNED);
			case LEFT -> {
				if (room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
					throw new RoomException(RoomErrorCode.FULL_CAPACITY);
				}
				hostUser.setStatus(HostUserStatus.JOINED);
				room.getHostUsers().add(hostUser);
				room.setCurPaticipants(room.getCurPaticipants() + 1);
			}
			case JOINED -> {
				List<MessageResponseDTO> messages = chatRepository.chatListByRoom(roomId).stream()
						.map(MessageResponseDTO::new).toList();
				return new RoomWithChatDTO(messages, new RoomDTO(room, true));
			}
			}
		} else {
			if (room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
				throw new RoomException(RoomErrorCode.FULL_CAPACITY);
			}

			hostUser = hostUserRepository.save(new HostUserEntity(room, user));
			room.getHostUsers().add(hostUser);
			room.setCurPaticipants(room.getCurPaticipants() + 1);
		}

		List<MessageResponseDTO> messages = chatRepository.chatListByRoom(roomId).stream().map(MessageResponseDTO::new)
				.toList();

		return new RoomWithChatDTO(messages, new RoomDTO(room, true));
	}

	public void leftRoom(String roomId, String email) {
		log.info("********** deleteRoom() 호출 - roomId: {}, email: {} **********", roomId, email);

		RoomEntity room = utils.findRoom(roomId);
		UserEntity user = utils.findUser(email);
		if (!room.getCreateUser().getUserEmail().equals(email)) {
			Optional<HostUserEntity> hostUser = hostUserRepository.findLoginId(roomId, user.getUserId());
			if (hostUser.isPresent()) {
				hostUser.get().setStatus(HostUserStatus.LEFT);
				room.setCurPaticipants(room.getCurPaticipants() - 1);
				return;
			}
			throw new RoomException(RoomErrorCode.NO_PERMISSION);
		}

		roomRepository.delete(room);
	}

	public RoomDTO updateRoom(RoomDTO room, String roomId, String email) {
		log.info("********** updateRoom() 호출 - roomId: {}, email: {} **********", roomId, email);
		UserEntity user = utils.findUser(email);
		RoomEntity roomEntity = utils.findRoom(roomId);
		if (!(roomEntity.getCreateUser().getUserId()==user.getUserId())) {
			throw new RoomException(RoomErrorCode.NO_PERMISSION);
		}

		roomEntity.setRoomContent(room.getRoomContent());
		roomEntity.setCurPaticipants(room.getCurPaticipants());
		roomEntity.setRoomModifiedDate(LocalDateTime.now());
		roomEntity.setRoomTitle(room.getRoomTitle());
		roomEntity.setRoomType(room.getRoomType());

		log.info("✅ 방 수정 완료 - roomId: {}", roomId);
		return new RoomDTO(roomEntity);
	}

	public List<RoomDTO> getMyJoinRooms(String email) {
		UserEntity user = utils.findUser(email);
		List<HostUserEntity> hosts = hostUserRepository.findRoomsByUser(user.getUserId());
		return hosts.stream().map(RoomDTO::new).toList();
	}

	public void bannedUser(Long bannedUserId, String roomId, String email) {
		UserEntity host = utils.findUser(email);
		HostUserEntity hostUser = utils.findHostUser(roomId, bannedUserId);
		if (!(hostUser.getRoom().getCreateUser().getUserId() == host.getUserId())) {
			throw new RoomException(RoomErrorCode.IS_ONLY_HOST);
		}
		hostUser.setStatus(HostUserStatus.BANNED);
		hostUser.getRoom().setCurPaticipants(hostUser.getRoom().getCurPaticipants() - 1);
	}
}
