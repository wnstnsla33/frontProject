package pProject.pPro.room.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.ChatEntity;
import pProject.pPro.room.DTO.ChatMessageDTO;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long>{
	@Query("select c from ChatEntity c where c.room.roomId=:roomId")
	List<ChatEntity> chatListByRoom(@Param("roomId")String roomId);
}	
