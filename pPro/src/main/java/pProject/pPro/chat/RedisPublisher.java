package pProject.pPro.chat;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pProject.pPro.chat.DTO.ChatMessageDTO;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
	private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String roomId, ChatMessageDTO message) {
        redisTemplate.convertAndSend("chatroom:" + roomId, message);
    }
}
