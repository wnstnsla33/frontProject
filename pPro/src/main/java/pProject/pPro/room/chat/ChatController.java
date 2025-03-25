package pProject.pPro.room.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.room.DTO.ChatMessageDTO;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final RedisPublisher redisPublisher;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDTO message) {
        log.info("메시지 수신: {}", message);

        // 실시간 전송
        redisPublisher.publish(message.getRoomId().toString(), message);
        
        // (다음 단계) DB 저장은 여기에 붙일 수도 있고 분리할 수도 있음
    }
}