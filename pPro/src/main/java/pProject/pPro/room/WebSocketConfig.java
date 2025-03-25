package pProject.pPro.room;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	 @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
	        config.enableSimpleBroker("/sub"); // 브로커가 클라이언트로 보내는 주소
	        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 메시지 보낼 때 붙이는 주소
	    }

	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        registry.addEndpoint("/ws-stomp") // 연결 주소
	                .setAllowedOriginPatterns("*") // CORS 허용
	                .withSockJS(); // 웹소켓이 안 되는 환경에서도 대응 (fallback)
	    }
}
