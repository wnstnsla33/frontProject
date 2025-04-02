package pProject.pPro.securityConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import pProject.pPro.User.UserDTO;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
	private final UserRepository userRepository;
	public CustomOAuth2UserService(UserRepository userRepository) {
		this.userRepository= userRepository;
	}
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registraionid=userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;
        if (registraionid.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
      
        else {
        	return null;
        }
        String userEmail = oAuth2Response.getProvider()+" "+oAuth2Response.getproviderId();
        Optional< UserEntity> optionalUser = userRepository.findByEmail(userEmail);
        if(!optionalUser.isPresent()) {
        	UserEntity userEntity = new UserEntity();
        	userEntity.setUserName(oAuth2Response.getName());
        	userEntity.setUserBirthDay(oAuth2Response.getBirthDay());
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
        	String createDate = formatter.format(new Date());
        	userEntity.setUserCreateDate(createDate);
        	userEntity.setUserGrade(Grade.BRONZE);
        	userEntity.setUserEmail(userEmail);
        	userEntity.setUserAge(Integer.parseInt( oAuth2Response.getAge()));
        	System.out.println(oAuth2Response.getSex()+"***************오스서비스 확인 성별");
        	int num =0;
        	if(oAuth2Response.getSex().equals("M")) {
        		 num = (int)(Math.random() * 9) + 1;
        		userEntity.setUserSex("남성");
        	}
        	else {
        		 num = (int)(Math.random() * 6) + 10;
        		userEntity.setUserSex("여성");
        	}
        	userEntity.setUserImg("/uploads/classicImage/" + num + ".png");
        	userEntity.setUserLevel(1);
        	userEntity.setUserExp(0);
        	userRepository.save(userEntity);
        	UserDTO userDTO = new UserDTO();
        	userDTO.setEmail(userEmail);
        	userDTO.setName(oAuth2Response.getName());
        	userDTO.setRole("ROLE_USER");
        	return new CustomOAuth2User(userDTO);
        }
        else {
        	UserEntity existData = optionalUser.get();
        	if (!existData.getUserEmail().equals(userEmail)) {
                existData.setUserEmail(userEmail);
                System.out.println("oauth2 email바뀌어 새로 저장");
                userRepository.save(existData);
            }


             UserDTO userDTO = new UserDTO();
             userDTO.setEmail(userEmail);
             userDTO.setName(oAuth2Response.getName());
             if(existData.getUserGrade()==Grade.ADMIN) userDTO.setRole("ROLE_ADMIN");
             else userDTO.setRole("ROLE_USER");
             return new CustomOAuth2User(userDTO);
        }
        
	}

}
