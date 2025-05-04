package pProject.pPro.securityConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.UserEntity;

@Configuration
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	public CustomSuccessHandler(
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
		CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

		String username = customUserDetails.getUsername();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();
		
		String access = jwtUtil.createJwt("access", username, role, 60 * 60 * 1000L);
		String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
		response.addCookie(createCookie("access", access, 1));
		response.addCookie(createCookie("refresh", refresh, 2));
		 redisTemplate.opsForValue().set(
	                "refresh:" + username,
	                refresh,
	                7 * 24 * 60 * 60,
	                TimeUnit.SECONDS
	        );
		UserEntity userEntity = userRepository.findByEmail(username).get();
		userEntity.setRecentLoginTime(LocalDateTime.now());
		userRepository.save(userEntity);
		if (userEntity.getUserNickName() == null)
			response.sendRedirect("http://soribox.kro.kr/profileEdit");
		else
			response.sendRedirect("http://soribox.kro.kr");
	}

	private Cookie createCookie(String key, String value, int cookieType) {

		Cookie cookie = new Cookie(key, value);

		// cookie.setSecure(true);
		if (cookieType == 1) { // access
			cookie.setPath("/");
			cookie.setMaxAge(60 * 60); // 1시간 (3600초)
		} else { // refresh
			cookie.setPath("/auth");
			cookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (604800초)
		}
		cookie.setHttpOnly(false);

		return cookie;
	}
}
