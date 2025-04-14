package pProject.pPro.RoomUser;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pProject.pPro.entity.HostUserEntity;

public interface HostUserRepository extends JpaRepository<HostUserEntity, Long> {

	@Query("SELECT h FROM HostUserEntity h " + "JOIN FETCH h.room r "
			+ "WHERE r.roomId = :roomId AND h.user.userId = :userId")
	Optional<HostUserEntity> findLoginEmail(@Param("roomId") String roomId, @Param("userId") Long userId);

	@Query("SELECT h FROM HostUserEntity h " + "JOIN FETCH h.room r " + "WHERE h.user.userEmail = :userEmail")
	List<HostUserEntity> findRoomsByUser(@Param("userEmail") String userEmail);

	@Query("""
			    SELECT h FROM HostUserEntity h JOIN FETCH h.room r
			    JOIN FETCH r.createUser WHERE h.user.userId = :userId
			""")
	List<HostUserEntity> findRoomsByUserId(@Param("userId") Long userId);

	@Query("select count(h) from HostUserEntity h where h.user.userId=:userId")
	Long hostCount(@Param("userId") Long userId);
}
