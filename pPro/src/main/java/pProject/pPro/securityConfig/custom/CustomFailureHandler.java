package pProject.pPro.securityConfig.custom;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		if(exception instanceof BadCredentialsException) {
			writer.write("{\"error\": \"잘못된 비밀번호입니다.\"}");
		}else {
			writer.write("{\"error\": \"" + exception.getMessage() + "\"}");
		}writer.flush();
	}

}
