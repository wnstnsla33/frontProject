package pProject.pPro.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.RoomEntity;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String>{
	
}
