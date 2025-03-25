package pProject.pPro.room.chat;


import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.room.DTO.ChatMessageDTO;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

	@Override
	public void onMessage(Message redisMessage, byte[] pattern) {
		// TODO Auto-generated method stub
		try {
            String msgBody = new String(redisMessage.getBody(), StandardCharsets.UTF_8);
            ChatMessageDTO message = objectMapper.readValue(msgBody, ChatMessageDTO.class);

            log.info("Redis 수신 → /sub/chat/room/{}", message.getRoomId());

            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        } catch (Exception e) {
            log.error("RedisSubscriber 에러", e);
        }
	}
}