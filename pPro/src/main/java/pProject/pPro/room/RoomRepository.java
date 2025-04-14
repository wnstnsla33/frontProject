package pProject.pPro.room;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import pProject.pPro.entity.RoomEntity;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM RoomEntity r WHERE r.roomId = :roomId")
	Optional<RoomEntity> findByIdForUpdate(@Param("roomId") String roomId);

	@EntityGraph(attributePaths = { "hostUsers", "hostUsers.user" })
	@Query("SELECT r FROM RoomEntity r WHERE r.roomId = :roomId and r.hostUsers.status ='JOINED'")
	Optional<RoomEntity> fetchRoomWithHostUsers(@Param("roomId") String roomId);

	@EntityGraph(attributePaths = { "hostUsers", "hostUsers.user" })
	@Query("SELECT r FROM RoomEntity r WHERE (:title IS NULL OR r.roomTitle LIKE CONCAT('%', :title, '%')) "
			+ "AND (:roomType IS NULL OR r.roomType = :roomType) AND (:sido IS NULL OR r.address.sido = :sido) "
			+ "AND (:sigungu IS NULL OR r.address.sigungu = :sigungu) AND r.meetingTime >= CURRENT_DATE"
			+ "and r.hostUsers.status ='JOINED'")
	Page<RoomEntity> searchRooms(@Param("title") String title, @Param("roomType") String roomType,
			@Param("sido") String sido, @Param("sigungu") String sigungu, Pageable pageable);

	@EntityGraph(attributePaths = { "hostUsers", "hostUsers.user" })
	@Query("SELECT r FROM RoomEntity r " + "WHERE (:title IS NULL OR r.roomTitle LIKE CONCAT('%', :title, '%')) and r.hostUsers.status ='JOINED'")
	Page<RoomEntity> searchRooms(@Param("title") String title, Pageable pageable);

}
