package pProject.pPro.Admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.Admin.dto.AdminControllerResponseDTO;
import pProject.pPro.Admin.dto.AdminEnum;
import pProject.pPro.Admin.dto.AdminServiceResponseDTO;
import pProject.pPro.Admin.dto.AdminUserDTO;
import pProject.pPro.Admin.dto.SearchDTO;
import pProject.pPro.Admin.dto.UserChatByAdmin;
import pProject.pPro.Admin.dto.UserDetailByAdmimDTO;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.User.UserEnum;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.DTO.UserInfoDTO;
import pProject.pPro.User.DTO.UserServiceResponseDTO;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.PostRepository;
import pProject.pPro.post.DTO.PostListDTO;
import pProject.pPro.post.DTO.PostServiceDTO;
import pProject.pPro.reply.ReplyRepository;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.DTO.RoomDTO;
import pProject.pPro.room.chat.ChatRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;
	private final ChatRepository chatRepository;
	private final ReplyRepository replyRepository;
	private final HostUserRepository hostUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public AdminServiceResponseDTO deleteUserById(Long id) {
		try {
			userRepository.deleteById(id);
			return new AdminServiceResponseDTO(AdminEnum.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new AdminServiceResponseDTO(AdminEnum.FAIL);
		}
	}

	public AdminServiceResponseDTO getUserList(SearchDTO dto) {
		Pageable pageable = PageRequest.of(dto.getPage(), 10, Sort.by("userCreateDate").descending());
		Page<UserEntity> pageResult;

		if (dto.getName() != null && !dto.getName().isEmpty()) {
			pageResult = userRepository.findByUserNameContainingIgnoreCase(dto.getName(), pageable);
		} else {
			pageResult = userRepository.findAll(pageable);
		}

		List<AdminUserDTO> users = pageResult.getContent().stream().map(AdminUserDTO::new).collect(Collectors.toList());

		return new AdminServiceResponseDTO(AdminEnum.SUCCESS, users, pageResult.getTotalPages());
	}

	public AdminServiceResponseDTO<UserDetailByAdmimDTO> getUserDetailInfo(Long userId) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

		UserDetailByAdmimDTO userDetail = new UserDetailByAdmimDTO(userEntity);

		return new AdminServiceResponseDTO<>(AdminEnum.SUCCESS, userDetail);
	}

	public AdminServiceResponseDTO<List<PostListDTO>> getPostListByAdmin(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage(), 10, Sort.by("createDate").descending());

		Page<PostEntity> pageResult = postRepository
				.searchPostsByTitleOrUserName(searchDTO.getName() != null ? searchDTO.getName() : "", pageable);

		List<PostListDTO> postList = pageResult.getContent().stream().map(post -> new PostListDTO(post, true))
				.collect(Collectors.toList());

		return new AdminServiceResponseDTO<>(AdminEnum.SUCCESS, postList, pageResult.getTotalPages());
	}

	public AdminServiceResponseDTO<List<RoomDTO>> getRoomListByAdmin(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage(), 10, Sort.by("roomCreatDate").descending());

		Page<RoomEntity> pageResult = roomRepository.findAll(pageable);

		List<RoomDTO> roomList = pageResult.getContent().stream().map(room -> new RoomDTO(room, true))
				.collect(Collectors.toList());

		return new AdminServiceResponseDTO<>(AdminEnum.SUCCESS, roomList, pageResult.getTotalPages());
	}
	public AdminServiceResponseDTO<List<RoomDTO>> getUserRoomsByAdmin(Long userId) {
	    List<HostUserEntity> hostRooms = hostUserRepository.findRoomsByUserId(userId);
	    List<RoomDTO> rooms = hostRooms.stream()
	        .map(hu -> new RoomDTO(hu, true))
	        .collect(Collectors.toList());

	    return new AdminServiceResponseDTO<>(AdminEnum.SUCCESS, rooms);
	}


	public AdminServiceResponseDTO deleteRoom(String roomId) {
		try {
			roomRepository.deleteById(roomId);
			return new AdminServiceResponseDTO(AdminEnum.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new AdminServiceResponseDTO(AdminEnum.FAIL);
		}
	}

	public AdminServiceResponseDTO deletePostById(Long postId) {
		try {
			postRepository.deleteById(postId);
			return new AdminServiceResponseDTO(AdminEnum.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new AdminServiceResponseDTO(AdminEnum.FAIL);
		}
	}

	public AdminServiceResponseDTO deleteRoomByAdmin(String roomId) {
		try {
			roomRepository.deleteById(roomId);
			return new AdminServiceResponseDTO(AdminEnum.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new AdminServiceResponseDTO(AdminEnum.FAIL);
		}
	}

	public AdminServiceResponseDTO<List<UserChatByAdmin>> getUserChatsByAdmin(Long userId, int page, String keyword) {
	    Pageable pageable = PageRequest.of(page, 20, Sort.by("createTime").descending());

	    Page<ChatEntity> chatPage = chatRepository.searchUserChatsWithRoomTitle(userId, keyword, pageable);

	    List<UserChatByAdmin> chatList = chatPage.getContent().stream()
	        .map(UserChatByAdmin::new)
	        .collect(Collectors.toList());

	    return new AdminServiceResponseDTO<>(AdminEnum.SUCCESS, chatList, chatPage.getTotalPages());
	}

	@EventListener(ApplicationReadyEvent.class)
	public void createAdmin() {
		UserEntity user = new UserEntity();
		user.setUserEmail("admin@naver.com");
		user.setUserPassword(passwordEncoder.encode("adminadmin1234"));
		user.setUserGrade(Grade.ADMIN);
		user.setUserName("관리자");
		user.setUserNickName("manager");
		user.setUserImg("/uploads/classicImage/1.png");
		userRepository.save(user);
	}
}
