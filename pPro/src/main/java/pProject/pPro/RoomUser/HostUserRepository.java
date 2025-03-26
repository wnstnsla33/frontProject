package pProject.pPro.RoomUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pProject.pPro.entity.HostUserEntity;

public interface HostUserRepository extends JpaRepository<HostUserEntity, Long>{
	@Query("select h from HostUserEntity h where h.room.roomId=:roomId and h.user.userEmail=:userEmail")
	Optional< HostUserEntity> findLoginEmail(@Param("roomId") String roomId,@Param("userEmail")String userEmail);
}
