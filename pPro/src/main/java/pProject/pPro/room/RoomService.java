package pProject.pPro.room;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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
import pProject.pPro.chat.RedisPublisher;
import pProject.pPro.chat.DTO.ChatMessageDTO;
import pProject.pPro.chat.DTO.ChatMessageDTO.MessageType;
import pProject.pPro.chat.DTO.MessageResponseDTO;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.MessageEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.message.MessageRepository;
import pProject.pPro.message.DTO.SaveMessageDTO;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.DTO.RoomEditInfo;
import pProject.pPro.room.DTO.RoomWithChatDTO;
import pProject.pPro.room.DTO.SearchRoomDTO;
import pProject.pPro.room.excption.RoomErrorCode;
import pProject.pPro.room.excption.RoomException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {
	private final RedisPublisher redisPublisher;
	private final RoomRepository roomRepository;
	private final HostUserRepository hostUserRepository;
	private final ChatRepository chatRepository;
	private final MessageRepository messageRepository;
	private final RoomQRepository roomQRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ServiceUtils utils;
	
	
	@Value("${admin.email}")
	private String adminEmail;
	public RoomDTO createRoom(RoomDTO room, String email) {
		UserEntity user = utils.findUser(email);
		RoomEntity roomEntity = new RoomEntity(room);
		roomEntity.setRoomImg(saveImage(room.getRoomSaveImg()));
		roomEntity.setCreateUser(user);
		roomEntity.setAddress(new RoomAddress(room.getSido(), room.getSigungu()));

		if (room.getSecretePassword() != null) {
			roomEntity.setSecretePassword(passwordEncoder.encode(room.getSecretePassword()));
		}

		RoomEntity saveRoom = roomRepository.save(roomEntity);
		try {
			hostUserRepository.save(new HostUserEntity(saveRoom, user));
		} catch (DataIntegrityViolationException e) {
			throw new RoomException(RoomErrorCode.DUPLICATE_ROOM);
		}
		return new RoomDTO(saveRoom);
	}


	public RoomDTO findRoom(String roomId) {
		return new RoomDTO(roomRepository.findByIdForUpdate(roomId).orElseThrow(() -> {
			return new RoomException(RoomErrorCode.ROOM_NOT_FOUND);
		}));
	}

	public List<RoomDTO> roomList() {
		return roomRepository.findAll().stream().map(RoomDTO::new).toList();
	}

	public Page<RoomDTO> searchRooms(SearchRoomDTO dto) {

		// ✅ 빈 문자열 → null 변환
		if (dto.getTitle() != null && dto.getTitle().isBlank()) dto.setTitle(null);
		if (dto.getRoomType() != null && dto.getRoomType().isBlank()) dto.setRoomType(null);
		if (dto.getSido() != null && dto.getSido().isBlank()) dto.setSido(null);
		if (dto.getSigungu() != null && dto.getSigungu().isBlank()) dto.setSigungu(null);
		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("roomCreatDate").descending());

		return roomQRepository
			.searchRooms(dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu(), pageable)
			.map(RoomDTO::new);
	}


	public Page<RoomDTO> searchRooms(SearchRoomDTO dto, String email) {
		UserEntity user = utils.findUser(email);

		// ✅ 유저 주소값 자동 설정
		dto.setSido(user.getAddress().getSido());
		dto.setSigungu(user.getAddress().getSigungu());
		
		// ✅ 빈 문자열 → null 변환
		if (dto.getTitle() != null && dto.getTitle().isBlank()) dto.setTitle(null);
		if (dto.getRoomType() != null && dto.getRoomType().isBlank()) dto.setRoomType(null);
		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("roomCreatDate").descending());
		return roomQRepository
			.searchRooms(dto.getTitle(), dto.getRoomType(), dto.getSido(), dto.getSigungu(), pageable)
			.map(RoomDTO::new);
	}


	public RoomWithChatDTO joinRoom(String roomId, String email) {

	    boolean isAdmin = email.equals(adminEmail);
	    UserEntity user = utils.findUser(email);
	    RoomEntity room = roomRepository.fetchRoomWithHostUsers(roomId)
	            .orElseThrow(() -> new RoomException(RoomErrorCode.INVALID_ID));

	    if (isAdmin) {//관리자일 경우 비밀번호와 상관없이 방에 들어감
	        List<MessageResponseDTO> adminMessages = chatRepository.chatListByRoom(roomId).stream()
	                .map(MessageResponseDTO::new)
	                .toList();
	        return new RoomWithChatDTO(adminMessages, new RoomDTO(room,true));
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
	                room.setCurPaticipants(room.getCurPaticipants() + 1);
	                publish(user, roomId, MessageType.ENTER, user.getUserNickName() + "님이 입장하였습니다.");
	            }
	            case JOINED -> {
	                // 이미 참여 중
	            }
	        }
	    } else {
	        if (room.getRoomMaxParticipants() <= room.getCurPaticipants()) {
	            throw new RoomException(RoomErrorCode.FULL_CAPACITY);
	        }
	        hostUser = hostUserRepository.save(new HostUserEntity(room, user));
	        room.getHostUsers().add(hostUser);
	        room.setCurPaticipants(room.getCurPaticipants() + 1);
	        publish(user, roomId, MessageType.ENTER, user.getUserNickName() + "님이 입장하였습니다.");
	    }

	    List<MessageResponseDTO> messages = chatRepository.chatListByRoom(roomId).stream()
	            .map(MessageResponseDTO::new)
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
				publish(user, roomId, MessageType.ENTER, user.getUserNickName()+"님이 입장하였습니다.");
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
			publish(user, roomId, MessageType.ENTER, user.getUserNickName()+"님이 입장하였습니다.");
		}

		List<MessageResponseDTO> messages = chatRepository.chatListByRoom(roomId).stream().map(MessageResponseDTO::new)
				.toList();

		return new RoomWithChatDTO(messages, new RoomDTO(room, true));
	}

	public void leftRoom(String roomId, String email) {
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
		UserEntity user = utils.findUser(email);
		RoomEntity roomEntity = utils.findRoom(roomId);
		if (!(roomEntity.getCreateUser().getUserId()==user.getUserId())) {
			throw new RoomException(RoomErrorCode.NO_PERMISSION);
		}

		roomEntity.setRoomContent(room.getRoomContent());
		roomEntity.setRoomMaxParticipants(room.getMaxParticipants());
		roomEntity.setRoomModifiedDate(LocalDateTime.now());
		roomEntity.setRoomTitle(room.getRoomTitle());
		roomEntity.setRoomType(room.getRoomType());

		return new RoomDTO(roomEntity);
	}

	public List<RoomDTO> getMyJoinRooms(String email) {
		UserEntity user = utils.findUser(email);
		List<HostUserEntity> hosts = hostUserRepository.findRoomsByUser(user.getUserId());
		return hosts.stream().map(RoomDTO::new).toList();
	}

	public void bannedUser(Long bannedUserId, String roomId, String email) {
		UserEntity host = utils.findUser(email);
		if(host.getUserId()==bannedUserId)throw new RoomException(RoomErrorCode.INVALID_REQUEST);
		HostUserEntity roomUser = utils.findHostUser(roomId, bannedUserId);
		if (!(roomUser.getRoom().getCreateUser().getUserId() == host.getUserId())) {
			throw new RoomException(RoomErrorCode.IS_ONLY_HOST);
		}
		roomUser.setStatus(HostUserStatus.BANNED);
		RoomEntity room = roomUser.getRoom();
		room.setCurPaticipants(room.getCurPaticipants() - 1);
		String msg = roomUser.getUser().getUserNickName() + " 님이 강퇴당했습니다.";
		publish(roomUser.getUser(), roomId, MessageType.BANNED, msg);
		//메세지 로직
		SaveMessageDTO dto = new SaveMessageDTO(room.getRoomTitle()+"에서 강퇴당했습니다.",
				msg, roomUser.getUser().getUserId(), pProject.pPro.message.DTO.MessageType.NOTICE);
		MessageEntity message = new MessageEntity(dto, host, roomUser.getUser());
		messageRepository.save(message);
	}
	
	public RoomEditInfo getRoomInfo(String  roomId) {
		return new RoomEditInfo(roomRepository.findById(roomId).orElseThrow(()->new RoomException(RoomErrorCode.INVALID_ID)));
	}
	
	 public void publish(UserEntity user, String roomId, MessageType type, String customMessage) {
	        ChatMessageDTO message = new ChatMessageDTO();
	        message.setType(type);
	        message.setRoomId(roomId);
	        message.setSenderName(user.getUserNickName());
	        message.setUserId(user.getUserId());
	        message.setUserImg(user.getUserImg());
	        message.setMessage(customMessage);
	        redisPublisher.publish(roomId, message);
	 }
	 
	 public String saveImage(MultipartFile imageFile) {
	    	String UPLOAD_DIR = "/home/ubuntu/uploads/";
			File dir = new File(UPLOAD_DIR);
			if (!dir.exists())
				dir.mkdirs();

			String originalFilename = imageFile.getOriginalFilename();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			String savedFileName = UUID.randomUUID() + extension;

			File savedFile = new File(UPLOAD_DIR + savedFileName);
			try {

				imageFile.transferTo(savedFile);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}

			return "/uploads/" + savedFileName;
		}
}
