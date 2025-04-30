package pProject.pPro.securityConfig;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import pProject.pPro.global.CommonResponse;
import pProject.pPro.securityConfig.exception.FilterErrorCode;
import pProject.pPro.securityConfig.exception.FilterException;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/auth/getToken")
    public ResponseEntity<?> getToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refresh");

        if (refreshToken == null) {
            clearCookies(response);
            throw new FilterException(FilterErrorCode.REQUIRED_LOGIN);
        }

        if (jwtUtil.isExpired(refreshToken)) {
            clearCookies(response);
            throw new FilterException(FilterErrorCode.EXPIRED_REFRESH);
        }
        
        long remainingMs = jwtUtil.getRemainingTime(refreshToken);
        logRemainingTime(remainingMs);
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String redisKey = "refresh:" + email;
        String savedRefreshToken = redisTemplate.opsForValue().get(redisKey);
        
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            clearCookies(response);
            throw new FilterException(FilterErrorCode.EXPIRED_REFRESH);
        }

        String newAccessToken = jwtUtil.createJwt("access", email, role, 10 * 60 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, remainingMs);

        redisTemplate.opsForValue().set(redisKey, newRefreshToken, remainingMs, TimeUnit.MILLISECONDS);

        response.addCookie(makeCookie("access", newAccessToken, 10 * 60, "/"));
        response.addCookie(makeCookie("refresh", newRefreshToken, (int) (remainingMs / 1000), "/auth"));

        return ResponseEntity.ok(CommonResponse.success("새 access + refresh 토큰이 발급되었습니다."));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private Cookie makeCookie(String name, String value, int maxAgeSeconds, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setPath(path);
        return cookie;
    }

    private void clearCookies(HttpServletResponse response) {
        response.addCookie(makeCookie("access", null, 0, "/"));
        response.addCookie(makeCookie("refresh", null, 0, "/auth"));
    }
    
    public void logRemainingTime(long remainingMs) {
        long totalSeconds = remainingMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        System.out.println("⏱️ 남은 시간: " + minutes + "분 " + seconds + "초 (" + remainingMs + "ms)");
    }
}
