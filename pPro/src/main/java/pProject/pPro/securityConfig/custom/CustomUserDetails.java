package pProject.pPro.securityConfig.custom;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import pProject.pPro.entity.Grade;
import pProject.pPro.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

    private String userEmail;
    private String userPassword;
    private Grade userGrade;
    private String userName;

    public CustomUserDetails(UserEntity userEntity) {
        this.userEmail = userEntity.getUserEmail();
        this.userPassword = userEntity.getUserPassword();
        this.userGrade = userEntity.getUserGrade();
        this.userName = userEntity.getUserName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> {
            if (userGrade == Grade.ADMIN) {
                return "ROLE_ADMIN";
            } else if (userGrade == Grade.BANNED) {
                return "ROLE_BANNED";
            } else {
                return "ROLE_USER";
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    public String getName() {
        return userName;
    }
}
