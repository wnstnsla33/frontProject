package pProject.pPro.chat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.chat.DTO.ChatMessageDTO;
import pProject.pPro.entity.ChatEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long>{
	@Query("select c from ChatEntity c where c.room.roomId=:roomId")
	List<ChatEntity> chatListByRoom(@Param("roomId")String roomId);
	@Query("SELECT c FROM ChatEntity c JOIN FETCH c.room r " +
		       "WHERE c.user.userId = :userId AND LOWER(r.roomTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "ORDER BY c.createTime DESC")
		Page<ChatEntity> searchUserChatsWithRoomTitle(
		    @Param("userId") Long userId,
		    @Param("keyword") String keyword,
		    Pageable pageable
		);
}	
