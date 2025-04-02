package pProject.pPro.securityConfig;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import pProject.pPro.securityConfig.custom.CustomFailureHandler;
import pProject.pPro.securityConfig.custom.CustomSuccessHandlerNoSNS;
import pProject.pPro.securityConfig.custom.CustomUsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;
    private final CustomSuccessHandlerNoSNS customSuccessHandlerNoSNS;
    private final JWTUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // 프론트 주소 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // OPTIONS 추가
        config.setAllowCredentials(true); // 쿠키 허용
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        config.setExposedHeaders(List.of("Set-Cookie", "refresh","access")); // 노출할 헤더
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // CustomUsernamePasswordAuthenticationFilter 필터 생성
        CustomUsernamePasswordAuthenticationFilter customFilter = new CustomUsernamePasswordAuthenticationFilter();
        customFilter.setAuthenticationManager(authenticationManager); // AuthenticationManager 설정
        customFilter.setFilterProcessesUrl("/login"); // 로그인 URL 설정
        customFilter.setAuthenticationSuccessHandler(customSuccessHandlerNoSNS);
        customFilter.setAuthenticationFailureHandler(customFailureHandler);

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (테스트용)
            .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class) // Custom 필터 추가
            .httpBasic(httpBasic -> httpBasic.disable()) // HTTP 기본 인증 비활성화
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(customSuccessHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws-stomp/**", "/api/**","/", "/my", "/signup/**","/find/**","/post","/auth/getToken").permitAll() // 공개 경로
                .requestMatchers("/post/**","/myInfo","/user/**","/chatRoom/**","/auth/logout").hasAnyRole("USER","ADMIN") // USER 역할 필요
                .requestMatchers("/admin/**","/admin").hasRole("ADMIN")
                .anyRequest().authenticated() // 나머지 경로는 인증 필요
            )
            .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함
            );

        return http.build();
    }
}
