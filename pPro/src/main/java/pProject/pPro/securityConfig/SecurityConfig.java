package pProject.pPro.securityConfig;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
        config.setAllowedOrigins(List.of("http://15.164.75.149:8080","http://soribox.kro.kr")); // 프론트 주소 허용
        config.setAllowedMethods(List.of("*")); // OPTIONS 추가
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
        // 🔧 Custom 로그인 필터
        CustomUsernamePasswordAuthenticationFilter customFilter = new CustomUsernamePasswordAuthenticationFilter();
        customFilter.setAuthenticationManager(authenticationManager);
        customFilter.setFilterProcessesUrl("/login");
        customFilter.setAuthenticationSuccessHandler(customSuccessHandlerNoSNS);
        customFilter.setAuthenticationFailureHandler(customFailureHandler);

        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            
            // ✅ [1] 전역 요청 로깅 필터 추가 (모든 요청 URI 출력)

            // ✅ [2] 커스텀 로그인 필터 추가
            .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)

            .httpBasic(httpBasic -> httpBasic.disable())
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(customSuccessHandler)

            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws-stomp/**", "/api/**","/",  "/signup/**","/find/**","/post","/auth/getToken","/uploads/**","/post/**","/chatRoom/search").permitAll()
                .requestMatchers("/user").hasAnyRole("USER","ADMIN","BANNED")
                .requestMatchers("/user/**","/chatRoom/**","/auth/logout","/report/**","/friends/**").hasAnyRole("USER","ADMIN")
                .requestMatchers("/admin/**","/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

}
