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
		}catch (DataIntegrityViolationException e) {
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
		return new RoomDTO(roomRepository.findByIdForUpdate(roomId)
				.orElseThrow(() -> {
					log.warn("🚫 방 조회 실패 - 존재하지 않음: {}", roomId);
					return new RoomException(RoomErrorCode.ROOM_NOT_FOUND);
				}));
	}

	public List<RoomDTO> roomList() {
		log.info("********** roomList() 호출 **********");
		return roomRepository.findAll().stream().map(RoomDTO::new).toList();
	}

	public Page<RoomDTO> searchRooms(SearchRoomDTO dto) {
		log.info("********** searchRooms() 호출 - title: {}, type: {}, sido: {}, sigungu: {} **********",
				dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu());

		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("roomCreatDate").descending());
		return roomRepository.searchRooms(dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu(), pageable)
				.map(RoomDTO::new);
	}

	public RoomDTO joinRoom(String roomId, String email) {
		log.info("********** joinRoom() 호출 - roomId: {}, email: {} **********", roomId, email);

		boolean isAdmin = email.equals("admin@naver.com");
		Optional<HostUserEntity> findHostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (findHostUser.isPresent()) {
			log.info("⚠️ 이미 참여중인 유저 - roomId: {}, email: {}", roomId, email);
			return new RoomDTO(findHostUser.get());
		}

		RoomEntity room = utils.findRoom(roomId);
		if (!isAdmin && room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
			log.warn("🚫 방 인원 초과 - roomId: {}", roomId);
			throw new RoomException(RoomErrorCode.FULL_CAPACITY);
		}

		UserEntity user = utils.findUser(email);
		hostUserRepository.save(new HostUserEntity(room, user));
		if (!isAdmin) room.setCurPaticipants(room.getCurPaticipants() + 1);

		log.info("✅ 방 입장 완료 - user: {}, room: {}", user.getUserName(), room.getRoomTitle());
		return new RoomDTO(room);
	}

	public RoomDTO joinPwdRoom(String roomId, String email, String pwd) {
		log.info("********** joinPwdRoom() 호출 - roomId: {}, email: {} **********", roomId, email);

		RoomEntity room = utils.findRoom(roomId);
		if (!passwordEncoder.matches(pwd, room.getSecretePassword())) {
			log.warn("🚫 비밀번호 불일치 - roomId: {}", roomId);
			throw new RoomException(RoomErrorCode.INVALID_PASSWORD);
		}

		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (hostUser.isPresent()) {
			log.info("⚠️ 이미 참여중인 유저 - 비밀방 - email: {}", email);
			return new RoomDTO(hostUser.get(), true);
		}

		if (room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
			log.warn("🚫 방 인원 초과 - roomId: {}", roomId);
			throw new RoomException(RoomErrorCode.FULL_CAPACITY);
		}

		UserEntity user = utils.findUser(email);
		hostUserRepository.save(new HostUserEntity(room, user));
		room.setCurPaticipants(room.getCurPaticipants() + 1);

		log.info("✅ 비밀번호 입장 성공 - user: {}, room: {}", user.getUserName(), room.getRoomTitle());
		return new RoomDTO(room, true);
	}

	public void deleteRoom(String roomId, String email) {
		log.info("********** deleteRoom() 호출 - roomId: {}, email: {} **********", roomId, email);

		RoomEntity room = utils.findRoom(roomId);
		if (!room.getCreateUser().getUserEmail().equals(email)) {
			Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
			if (hostUser.isPresent()) {
				hostUserRepository.delete(hostUser.get());
				room.setCurPaticipants(room.getCurPaticipants() - 1);
				log.info("🚪 유저 방 퇴장 처리 완료 - email: {}", email);
				return;
			}
			log.warn("🚫 방 삭제 권한 없음 - email: {}", email);
			throw new RoomException(RoomErrorCode.NO_PERMISSION);
		}

		roomRepository.delete(room);
		log.info("🗑️ 방 삭제 완료 - roomId: {}", roomId);
	}

	public RoomDTO updateRoom(RoomDTO room, String roomId, String email) {
		log.info("********** updateRoom() 호출 - roomId: {}, email: {} **********", roomId, email);

		RoomEntity roomEntity = utils.findRoom(roomId);
		if (!roomEntity.getCreateUser().getUserEmail().equals(email)) {
			log.warn("🚫 방 수정 권한 없음 - email: {}", email);
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
		log.info("********** getMyJoinRooms() 호출 - email: {} **********", email);
		List<HostUserEntity> hosts = hostUserRepository.findRoomsByUser(email);
		log.info("📦 참여 중인 방 수: {}", hosts.size());
		return hosts.stream().map(RoomDTO::new).toList();
	}

	public List<MessageResponseDTO> getChatList(String roomId, String email) {
		log.info("********** getChatList() 호출 - roomId: {}, email: {} **********", roomId, email);

		Optional<HostUserEntity> hostUser = hostUserRepository.findLoginEmail(roomId, email);
		if (hostUser.isEmpty()) {
			log.warn("🚫 채팅 접근 권한 없음 - email: {}", email);
			throw new RoomException(RoomErrorCode.NOT_JOINED);
		}

		List<MessageResponseDTO> messages = chatRepository.chatListByRoom(roomId).stream()
				.map(MessageResponseDTO::new)
				.collect(Collectors.toList());

		log.info("💬 채팅 메시지 수: {}", messages.size());
		return messages;
	}
}
