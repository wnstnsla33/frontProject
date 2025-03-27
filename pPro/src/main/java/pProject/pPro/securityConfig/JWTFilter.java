package pProject.pPro.securityConfig;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pProject.pPro.User.UserDTO;

public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("access")) {
					token = cookie.getValue();
					break;
				}
			}
		}

		String requestURI = request.getRequestURI();

		// 허용할 경로는 무조건 필터를 통과시킴
		boolean isAllowedPath = requestURI.equals("/post") || requestURI.matches("/post/\\d+")
				|| requestURI.equals("/signup") || requestURI.equals("/signup/confirm") || requestURI.equals("/login")
				|| requestURI.equals("/my") || requestURI.startsWith("/find") || requestURI.equals("/auth/getToken")
				|| requestURI.equals("/ws-stomp") || // WebSocket 연결
				requestURI.startsWith("/ws-stomp"); // SockJS fallback 지원 (필수)
		if (isAllowedPath) {
			filterChain.doFilter(request, response);
			return;
		}
		// 여기서부터는 토큰이 필수적임
		if (token == null) {
			System.out.println("널값 토쿤");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"message\": \"access 토큰이 만료되었습니다.\"}");
			return;
		}

		try {
			if (jwtUtil.isExpired(token)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "access 토큰이 만료되었습니다.");
				return;
			}
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			System.out.println("토큰 널");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"message\": \"access 토큰이 만료되었습니다.\"}");
			return;
		}

		// 인증 정보 등록
		String email = jwtUtil.getEmail(token);
		String role = jwtUtil.getRole(token);

		UserDTO userDTO = new UserDTO();
		userDTO.setEmail(email);
		userDTO.setRole(role);
		
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
				customOAuth2User.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}

}
