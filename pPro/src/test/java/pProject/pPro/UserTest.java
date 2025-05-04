package pProject.pPro;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.entity.UserExp.BonusExpStrategy;
import pProject.pPro.post.PostService;
import pProject.pPro.post.DTO.WritePostDTO;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;
    @Test
    @DisplayName("유저 닉네임 Unique적용 확인")
    void duplicateNickname_concurrently() throws InterruptedException {
        String duplicateNick = "sameNickname";
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                UserEntity user = new UserEntity("email" + Math.random() + "@test.com");
                user.setUserNickName(duplicateNick);
                userRepository.save(user);
            } catch (Exception e) {
                System.out.println("등록 실패: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        };

        executor.submit(task);
        executor.submit(task);

        latch.await();

        long count = userRepository.countByUserNickName(duplicateNick);
        assertTrue(count == 1);
    }
    @Test
    @DisplayName("exp 경험치 증가량 확인")
    void ExpTest() {
    	UserEntity user = new UserEntity("testExp@naver.com");
    	user.expUp();
    	user.setExpStrategy(new BonusExpStrategy());
    	user.expUp();
    	assertEquals(50, user.getUserExp());
    	System.out.println(user.getUserExp()+"정상적으로 30증가 확인");
    }
}
