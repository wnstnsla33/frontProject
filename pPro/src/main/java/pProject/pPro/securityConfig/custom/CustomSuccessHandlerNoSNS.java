package pProject.pPro.securityConfig.custom;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pProject.pPro.securityConfig.JWTUtil;

@Component
public class CustomSuccessHandlerNoSNS extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;

	public CustomSuccessHandlerNoSNS(JWTUtil jwtUtil) {

		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("인증 성공!");
		UserDetails customUserDetails = (UserDetails) authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String access = jwtUtil.createJwt("access",username, role, 60 * 60 * 1000L);
		String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
		response.addCookie(createCookie("access", access,1));
		response.addCookie(createCookie("refresh", refresh,2));
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"message\": \"로그인 성공!\"}");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	private Cookie createCookie(String key, String value,int cookieType) {

		Cookie cookie = new Cookie(key, value);
		if (cookieType == 1) { // access
			cookie.setPath("/");
			cookie.setMaxAge(60 * 60); // 1시간 (3600초)
		} else { // refresh
			cookie.setPath("/auth");
			cookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (604800초)
		}
		cookie.setHttpOnly(true);

		return cookie;
	}
}
