package pProject.pPro.message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.MessageEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.message.DTO.MessageListDTO;
import pProject.pPro.message.DTO.MessageResponseDTO;
import pProject.pPro.message.DTO.SaveMessageDTO;
import pProject.pPro.message.exception.MessageErrorCode;
import pProject.pPro.message.exception.MessageExeption;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final ServiceUtils utils;

	public void save(SaveMessageDTO dto, String email) {
		System.out.println(dto.getType());
		UserEntity sender = utils.findUser(email);
		UserEntity receiver = utils.findUserById(dto.getReceiverId());
		MessageEntity message = new MessageEntity(dto, sender, receiver);
		messageRepository.save(message);
	}

	public MessageListDTO getReceivedMessages(String email, String keyword, Pageable pageable) {
		Long userId = utils.findUser(email).getUserId();
		Page<MessageResponseDTO> page = messageRepository.getMyMsgList(userId, keyword, pageable)
				.map(MessageResponseDTO::new);
		int notReadMsgCount = messageRepository.notReadMsgCount(userId);
		MessageListDTO dto = new MessageListDTO(page.getContent(), page.getTotalPages(), notReadMsgCount);
		return dto;
	}

	// 보낸 메시지
	public MessageListDTO getSentMessages(String email, String keyword, Pageable pageable) {
		Long userId = utils.findUser(email).getUserId();
		Page<MessageResponseDTO> page = messageRepository.getMySendMsgList(userId, keyword, pageable)
				.map(MessageResponseDTO::new);
		MessageListDTO list = new MessageListDTO(page.getContent(), page.getTotalPages(), 0);
		return list;
	}

	public int unreadMessageCount(String email) {
		UserEntity user = utils.findUser(email);
		return messageRepository.notReadMsgCount(user.getUserId());
	}

	public MessageResponseDTO messageDetail(Long msgId, String email) {
		Long userId = utils.findUser(email).getUserId();
		MessageEntity msg = messageRepository.messageDetail(msgId)
				.orElseThrow(() -> new MessageExeption(MessageErrorCode.INVIALD_ID));
		if (msg.getSender().getUserId() == userId || msg.getReceiver().getUserId() == userId) {
			msg.setRead(true);
			return new MessageResponseDTO(msg);
		}
		throw new MessageExeption(MessageErrorCode.INVALID_USER_ID);
	}

}
