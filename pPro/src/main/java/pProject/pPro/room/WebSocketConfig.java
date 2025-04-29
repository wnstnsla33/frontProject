package pProject.pPro.room;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import pProject.pPro.securityConfig.JwtTokenProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setHandshakeHandler(new CustomHandshakeHandler())
                // .addInterceptors(jwtCookieHandshakeInterceptor()) // ← 일단 막고 테스트
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }


    @Bean
    public HandshakeInterceptor jwtCookieHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) {
                if (request instanceof ServletServerHttpRequest servletRequest) {
                    HttpServletRequest httpRequest = servletRequest.getServletRequest();
                    Cookie[] cookies = httpRequest.getCookies();

                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals("access")) {
                            	
                                String token = cookie.getValue();
                                if (jwtTokenProvider.validateToken(token)) {
                                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                                    attributes.put("user", auth.getPrincipal());
                                }
                                
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }

    public static class CustomHandshakeHandler extends DefaultHandshakeHandler {
        @Override
        protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                          Map<String, Object> attributes) {
            Object user = attributes.get("user");
            if (user instanceof UserDetails userDetails) {
                return new StompPrincipal(userDetails.getUsername());
            }
            return null;
        }
    }

    public static class StompPrincipal implements Principal {
        private final String name;

        public StompPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
