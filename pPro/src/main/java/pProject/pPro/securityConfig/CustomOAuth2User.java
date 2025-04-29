package pProject.pPro.securityConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import pProject.pPro.User.DTO.UserDTO;


public class CustomOAuth2User implements OAuth2User,UserDetails{

	private final UserDTO user;
	public CustomOAuth2User(UserDTO user) {
		this.user=user;
	}
	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<GrantedAuthority>();
		collection.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}	
		});
		return collection;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return user.getName();
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}


}
