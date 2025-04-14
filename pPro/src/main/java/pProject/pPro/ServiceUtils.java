package pProject.pPro;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pProject.pPro.Report.ReportRepository;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.exception.ReportErrorCode;
import pProject.pPro.Report.exception.ReportException;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.bookmark.BookmarkRepository;
import pProject.pPro.chat.ChatRepository;
import pProject.pPro.chat.exception.ChatErrorCode;
import pProject.pPro.chat.exception.ChatException;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.FriendsEntity;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.MessageEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.ReportEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.friends.FriendsRepository;
import pProject.pPro.friends.exception.FriendsErrorCode;
import pProject.pPro.friends.exception.FriendsException;
import pProject.pPro.message.MessageRepository;
import pProject.pPro.message.exception.MessageErrorCode;
import pProject.pPro.message.exception.MessageExeption;
import pProject.pPro.post.PostRepository;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;
import pProject.pPro.reply.ReplyRepository;
import pProject.pPro.reply.exception.ReplyErrorCode;
import pProject.pPro.reply.exception.ReplyException;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.excption.RoomErrorCode;
import pProject.pPro.room.excption.RoomException;

@Component
@RequiredArgsConstructor
public class ServiceUtils {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final RoomRepository roomRepository;
	private final ChatRepository chatRepository;
	private final ReplyRepository replyRepository;
	private final HostUserRepository hostUserRepository;
	private final ReportRepository reportRepository;
	private final MessageRepository messageRepository;
	private final FriendsRepository friendsRepository;
	
	public boolean isSocialAccount(String email) {
		return email.startsWith("naver ") || email.startsWith("google ") || email.startsWith("kakao ");
	}

	public UserEntity findUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL));
	}

	public Optional<UserEntity> findUserOptional(String email) {
		return userRepository.findByEmail(email);
	}

	public UserEntity findUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL));
	}

	public RoomEntity findRoom(String roomId) {
		return roomRepository.findByIdForUpdate(roomId).orElseThrow(() -> new RoomException(RoomErrorCode.INVALID_ID));
	}

	public PostEntity findPost(Long postId) {
		return postRepository.getPostDetail(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
	}

	public ReplyEntity findReply(Long replyId) {
		return replyRepository.findById(replyId).orElseThrow(() -> new ReplyException(ReplyErrorCode.REPLY_NOT_FOUND));
	}

	public ChatEntity findChat(Long chatId) {
		return chatRepository.findById(chatId).orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_CHAT));
	}

	
	public ReportEntity findReportWithUser(Long reportId) {
		return reportRepository.findReport(reportId).orElseThrow(() -> new ReportException(ReportErrorCode.INVALID_ID));
	}

	private static final String UPLOAD_DIR = "C:/myproject/uploads/";

	public String saveImage(MultipartFile imageFile) {
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

	public UserEntity findUser(String email, String errMsg) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL, errMsg));
	}

	public Optional<UserEntity> findUserOptional(String email, String errMsg) {
		return userRepository.findByEmail(email);
	}

	public UserEntity findUserById(Long userId, String errMsg) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL, errMsg));
	}

	public RoomEntity findRoom(String roomId, String errMsg) {
		return roomRepository.findByIdForUpdate(roomId)
				.orElseThrow(() -> new RoomException(RoomErrorCode.INVALID_ID, errMsg));
	}

	public PostEntity findPost(Long postId, String errMsg) {
		return postRepository.getPostDetail(postId)
				.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND, errMsg));
	}

	public ReplyEntity findReply(Long replyId, String errMsg) {
		return replyRepository.findById(replyId)
				.orElseThrow(() -> new ReplyException(ReplyErrorCode.REPLY_NOT_FOUND, errMsg));
	}

	public ChatEntity findChat(Long chatId, String errMsg) {
		return chatRepository.findById(chatId).orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND_CHAT));
	}
	
	public FriendsEntity findFriendsEntity(Long fId) {
		return friendsRepository.findById(fId).orElseThrow(()->new FriendsException(FriendsErrorCode.NOT_FOUND_FRIENDS_ID));
	}
}
