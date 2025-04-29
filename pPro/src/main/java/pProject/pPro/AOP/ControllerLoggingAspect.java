package pProject.pPro.AOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerLoggingAspect {
	private final HttpServletRequest request;
	@Before("execution(* pProject.pPro..*Controller.*(..))")
	public void logRequest(JoinPoint joinPoint) {
	    String uri = request.getRequestURI();
	    String username = "비로그인";

	    // 1. SecurityContext에서 인증 정보 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication != null && authentication.isAuthenticated()) {
	        Object principal = authentication.getPrincipal();
	        if (principal instanceof UserDetails userDetails) {
	            username = userDetails.getUsername();
	        }
	    }

	    log.info("🔔 [Controller 요청] URI: {}, 사용자: {}", uri, username);
	}
}
