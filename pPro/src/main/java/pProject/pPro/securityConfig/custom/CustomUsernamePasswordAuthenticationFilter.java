package pProject.pPro.securityConfig.custom;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pProject.pPro.User.DTO.SignupLoginDTO;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.stream.Collectors;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authenticationToken = null;

        String userId = null;
        String userPassword = null;

        // JSON 요청일 경우
        if (request.getContentType().equals(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
            try{
                // ObjectMapper를 이용해서 JSON 데이터를 dto에 저장 후 dto의 데이터를 이용

                SignupLoginDTO loginDto = objectMapper.readValue(
                        request.getReader().lines().collect(Collectors.joining()), SignupLoginDTO.class);

                userId = loginDto.getEmail();
                userPassword = loginDto.getPassword();

            } catch(IOException e){
                e.printStackTrace();
            }

        // POST 요청일 경우 기존과 같은 방식 이용
        } else if(request.getMethod().equals("POST")){
            userId = obtainUsername(request);
            userPassword = obtainPassword(request);

        }
        else {
            throw new AuthenticationServiceException("Authentication Method Not Supported : " + request.getMethod());
        }

        if(userId.equals("")){
            throw new AuthenticationServiceException("ID를 입력하지 않았습니다.");
        }
        else if(userPassword.equals("")) {
        	throw new AuthenticationServiceException("PW를 입력하지 않았습니다.");
        }

        authenticationToken = new UsernamePasswordAuthenticationToken(userId, userPassword);
        this.setDetails(request, authenticationToken);
        return this.getAuthenticationManager().authenticate(authenticationToken);

    }
}