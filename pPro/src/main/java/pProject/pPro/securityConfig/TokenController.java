package pProject.pPro.securityConfig;


import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pProject.pPro.securityConfig.JWTUtil;

@RestController
public class TokenController {

    private final JWTUtil jwtUtil;

    public TokenController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/auth/getToken")
    public ResponseEntity<?> getToken(HttpServletRequest request, HttpServletResponse response) {
    	
        // 1. refresh 토큰 꺼내기
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            refreshToken = Arrays.stream(cookies)
                    .filter(c -> "refresh".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh 토큰이 존재하지 않습니다.");
        }

        // 2. refresh 토큰 만료 체크
        try {
            if (jwtUtil.isExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh 토큰이 만료되었습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 refresh 토큰입니다.");
        }

        // 3. 새 access 토큰 생성
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String newAccessToken = jwtUtil.createJwt("access", email, role, 10 * 60 * 1000L); // 10분

        // 4. access 토큰을 쿠키로 클라이언트에게 다시 전달
        Cookie accessCookie = new Cookie("access", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(1 * 60); // 10분
        response.addCookie(accessCookie);
        System.out.println("토큰 재발급");
        return ResponseEntity.ok("새 access 토큰이 발급되었습니다.");
    }
}
