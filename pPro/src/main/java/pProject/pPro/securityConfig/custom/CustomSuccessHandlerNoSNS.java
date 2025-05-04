package pProject.pPro.securityConfig.custom;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.securityConfig.JWTUtil;

import java.util.concurrent.TimeUnit;

@Component
public class CustomSuccessHandlerNoSNS extends SimpleUrlAuthenticationSuccessHandler {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	public CustomSuccessHandlerNoSNS(
		JWTUtil jwtUtil,
		UserRepository userRepository,
		@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate
	) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
		this.redisTemplate = redisTemplate;
	}
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserDetails customUserDetails = (UserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        UserEntity user = userRepository.findByEmail(username).get();
        user.setRecentLoginTime(LocalDateTime.now());
        userRepository.save(user);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 🔐 Access & Refresh 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 60 * 60 * 1000L); // 1시간
        String refresh = jwtUtil.createJwt("refresh", username, role, 7 * 24 * 60 * 60 * 1000L); // 7일

        // ✅ Redis에 refresh 토큰 저장
        redisTemplate.opsForValue().set(
                "refresh:" + username,
                refresh,
                7 * 24 * 60 * 60,
                TimeUnit.SECONDS
        );

        // ✅ 쿠키에 토큰 추가
        response.addCookie(createCookie("access", access, 1));
        response.addCookie(createCookie("refresh", refresh, 2));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"로그인 성공!\"}");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private Cookie createCookie(String key, String value, int cookieType) {
        Cookie cookie = new Cookie(key, value);
        if (cookieType == 1) { // access
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 1시간
        } else { // refresh
            cookie.setPath("/auth");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        }
        cookie.setHttpOnly(true);
        return cookie;
    }
}
