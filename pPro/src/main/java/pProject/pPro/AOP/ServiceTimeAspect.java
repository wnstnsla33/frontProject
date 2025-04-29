package pProject.pPro.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ServiceTimeAspect {
	@Around("execution(* pProject.pPro..*Service.*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();  // 실제 서비스 메소드 실행
            return result;
        } finally {
            long end = System.currentTimeMillis();
            log.info("⏱️ [Service 실행시간] {} took {}ms", joinPoint.getSignature().toShortString(), (end - start));
        }
    }
}
