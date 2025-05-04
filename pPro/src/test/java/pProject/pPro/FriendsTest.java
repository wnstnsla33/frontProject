package pProject.pPro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.FriendsEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.friends.FriendsRepository;
import pProject.pPro.friends.FriendsService;
import pProject.pPro.friends.DTO.RequestFriendsType;

@SpringBootTest
@ActiveProfiles("test")
public class FriendsTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FriendsService friendService;
	@Autowired
	private FriendsRepository friendRepository;

	@Test
	@DisplayName("동시에 친구 삭제시 친구 수 확인")
	void deleteFriendsTest() throws InterruptedException {
		UserEntity user1 = userRepository.save(new UserEntity("user1@email.com"));
		UserEntity user2 = userRepository.save(new UserEntity("user2@email.com"));


		int threadCount = 5;
		friendService.requestFriends("user1@email.com", user2.getUserId());
		FriendsEntity fEntity = friendRepository.findFriendEntity(user1, user2);
		friendService.updateF("user2@email.com", fEntity.getFriendsId(), RequestFriendsType.ACCEPT);
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executor.execute(() -> {
				try {
					friendService.DeleteFreinds(fEntity.getFriendsId());
				} catch (Exception e) {
					System.out.println("삭제 실패: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		UserEntity result = userRepository.findByEmail(user1.getUserEmail()).orElseThrow();
		System.out.println("남은 친구 수: " + result.getFriendsCounts());
		assertEquals(0, result.getFriendsCounts());
	}
}
