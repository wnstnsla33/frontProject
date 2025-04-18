package pProject.pPro.securityConfig.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.UserEntity;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(username).get();

        // ✅ 예외 처리 추가
        if (userEntity == null) {
            throw new UsernameNotFoundException("해당 이메일의 계정을 찾을 수 없습니다: " + username);
        }

        return new CustomUserDetails(userEntity);
    }
}
