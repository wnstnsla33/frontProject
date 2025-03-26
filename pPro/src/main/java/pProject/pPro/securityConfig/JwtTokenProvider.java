package pProject.pPro.securityConfig;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // 인증된 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = jwtUtil.getEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            return !jwtUtil.isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
