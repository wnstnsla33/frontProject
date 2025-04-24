package pProject.pPro.securityConfig;

import java.util.Arrays;

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

    @GetMapping("/auth/getToken")
    public ResponseEntity<?> getToken(HttpServletRequest request, HttpServletResponse response) {

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
            deleteCookies(response); // 쿠키 삭제
            throw new FilterException(FilterErrorCode.REQUIRED_LOGIN);
        }

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                deleteCookies(response); // 만료된 경우 쿠키 삭제
                throw new FilterException(FilterErrorCode.EXPIRED_REFRESH);
            }
        } catch (Exception e) {
            deleteCookies(response); // 오류 시에도 쿠키 삭제
            throw new FilterException(FilterErrorCode.EXPIRED_REFRESH);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String newAccessToken = jwtUtil.createJwt("access", email, role, 10 * 60 * 1000L); // 10분

        Cookie accessCookie = new Cookie("access", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(10 * 60); // 10분
        response.addCookie(accessCookie);

        System.out.println("토큰 재발급");
        return ResponseEntity.ok("새 access 토큰이 발급되었습니다.");
    }

    private void deleteCookies(HttpServletResponse response) {
        Cookie deleteAccess = new Cookie("access", null);
        deleteAccess.setMaxAge(0);
        deleteAccess.setPath("/");

        Cookie deleteRefresh = new Cookie("refresh", null);
        deleteRefresh.setMaxAge(0);
        deleteRefresh.setPath("/");

        response.addCookie(deleteAccess);
        response.addCookie(deleteRefresh);
    }
}
