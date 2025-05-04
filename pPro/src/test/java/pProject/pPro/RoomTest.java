package pProject.pPro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import pProject.pPro.RoomUser.HostUserRepository;
import pProject.pPro.RoomUser.DTO.RoomAddress;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.Address;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.RoomService;

@SpringBootTest
@ActiveProfiles("test")
public class RoomConcurrencyTest {

    @Autowired private RoomService roomService;
    @Autowired private RoomRepository roomRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private HostUserRepository hostUserRepository;

    @Test
    @DisplayName("동시 참가 테스트 - 동시 10명 입장")
    void joinRoom_concurrently() throws InterruptedException {
        // === 테스트 준비 ===
        UserEntity host = userRepository.save(new UserEntity("host@email.comm"));
        String roomId = UUID.randomUUID().toString();

        RoomEntity room = new RoomEntity();
        room.setRoomId(roomId);
        room.setRoomTitle("동시 10명 입장시 테스트");
        room.setRoomMaxParticipants(20);
        room.setCurPaticipants(1);
        room.setCreateUser(host);
        room.setAddress(new RoomAddress("서울시", "은평구"));
        roomRepository.save(room);
        hostUserRepository.save(new HostUserEntity(room, host));

        for (int i = 0; i < 10; i++) {
            userRepository.save(new UserEntity("user" + i + "@naver.com"));
        }

        // === 동시 테스트 ===
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.execute(() -> {
                try {
                    roomService.joinRoom(roomId, "user" + idx + "@naver.com");
                } catch (Exception e) {
                    System.out.println("❌ join 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        RoomEntity resultRoom = roomRepository.findById(roomId).orElseThrow();
        System.out.println("최종 참가 인원 수: " + resultRoom.getCurPaticipants());
        assertEquals(11, resultRoom.getCurPaticipants());
    }

    @Test
    @DisplayName("최대 인원 제한 테스트 - 5명까지만 입장 허용")
    void joinRoom_exceedMaxParticipants() throws InterruptedException {
        // === 테스트 준비 ===
        UserEntity host = userRepository.save(new UserEntity("host2@email.comm"));
        String roomTestId = UUID.randomUUID().toString();

        RoomEntity room = new RoomEntity();
        room.setRoomId(roomTestId);
        room.setRoomTitle("최대 인원 테스트");
        room.setRoomMaxParticipants(5);
        room.setCurPaticipants(1);
        room.setCreateUser(host);
        room.setAddress(new RoomAddress("서울시", "은평구"));
        roomRepository.save(room);
        hostUserRepository.save(new HostUserEntity(room, host));

        for (int i = 10; i < 20; i++) {
            userRepository.save(new UserEntity("user" + i + "@naver.com"));
        }

        // === 동시 테스트 ===
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 10; i < 20; i++) {
            final int idx = i;
            executor.execute(() -> {
                try {
                    roomService.joinRoom(roomTestId, "user" + idx + "@naver.com");
                } catch (Exception e) {
                    System.out.println("❌ join 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        RoomEntity resultRoom = roomRepository.findById(roomTestId).orElseThrow();
        System.out.println("최종 참가 인원 수: " + resultRoom.getCurPaticipants());
        assertEquals(5, resultRoom.getCurPaticipants());
    }
}
