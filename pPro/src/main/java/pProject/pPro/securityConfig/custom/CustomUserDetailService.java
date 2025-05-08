package pProject.pPro.securityConfig.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private ServiceUtils utils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = utils.findUserLogin(username);

        // ✅ 예외 처리 추가

        return new CustomUserDetails(userEntity);
    }
}
