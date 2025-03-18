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
        System.out.println(username + " 계정 이메일 조회 중...");

        UserEntity userEntity = userRepository.findByEmail(username);

        // ✅ 예외 처리 추가
        if (userEntity == null) {
            System.out.println("❌ 해당 이메일의 계정을 찾을 수 없음: " + username);
            throw new UsernameNotFoundException("해당 이메일의 계정을 찾을 수 없습니다: " + username);
        }

        System.out.println("✅ 계정 조회 성공: " + username);
        return new CustomUserDetails(userEntity);
    }
}
