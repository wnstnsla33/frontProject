package pProject.pPro.room;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import pProject.pPro.entity.RoomEntity;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String>{
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM RoomEntity r WHERE r.roomId = :roomId")
	Optional< RoomEntity> findByIdForUpdate(@Param("roomId") String roomId);
}
