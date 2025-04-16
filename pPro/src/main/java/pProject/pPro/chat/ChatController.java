package pProject.pPro.chat;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.chat.DTO.ChatMessageDTO;
import pProject.pPro.chat.DTO.ChatMessageDTO.MessageType;
import pProject.pPro.room.RoomService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final RedisPublisher redisPublisher;

	private final ChatService chatService;
	private final RoomService roomService;
	
	@MessageMapping("/chat/message")
	public void message(ChatMessageDTO message,Principal principal) {
		// 실시간 전송
		redisPublisher.publish(message.getRoomId().toString(), message);
		chatService.saveMessage(message, principal.getName());
	}
	@MessageMapping("/chat/enter")
	public void enterChat(ChatMessageDTO message,Principal principal) {
		
		redisPublisher.publish(message.getRoomId().toString(), message);
	}
	@MessageMapping("/chat/delete")
	public void deleteChat(ChatMessageDTO message,Principal principal) {
		boolean isHost = chatService.isHost(message.getRoomId(), principal.getName());
		if(!isHost)redisPublisher.publish(message.getRoomId().toString(), message);
	}
}
