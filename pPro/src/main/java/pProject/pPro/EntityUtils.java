package pProject.pPro;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.Exception.UserErrorCode;
import pProject.pPro.User.Exception.UserException;
import pProject.pPro.bookmark.BookmarkRepository;
import pProject.pPro.chat.ChatRepository;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
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
public class EntityUtils {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final RoomRepository roomRepository;
	private final ChatRepository chatRepository;
	private final BookmarkRepository bookmarkRepository;
	private final ReplyRepository replyRepository;
	private final HostUserRepository hostUserRepository;
	public boolean isSocialAccount(String email) {
		return email.startsWith("naver ") || email.startsWith("google ") || email.startsWith("kakao ");
	}

	public UserEntity findUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.INVALID_EMAIL));
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
}
