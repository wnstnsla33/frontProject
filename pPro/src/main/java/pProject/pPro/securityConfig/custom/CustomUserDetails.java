package pProject.pPro.securityConfig.custom;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

	private UserEntity userEntity;
	public CustomUserDetails(UserEntity userEntity) {
		this.userEntity=userEntity;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<GrantedAuthority>();
		collection.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				if(userEntity.getUserGrade()==Grade.ADMIN) {
					return "ROLE_ADMIN";
				}
				else if(userEntity.getUserGrade()==Grade.BANNED)return "ROLE_BANNED";
				else {
					return "ROLE_USER";
				}
			}
		});	
		return collection;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return userEntity.getUserPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userEntity.getUserEmail();
	}
	public String getName() {
		return userEntity.getUserName();
	}

}
