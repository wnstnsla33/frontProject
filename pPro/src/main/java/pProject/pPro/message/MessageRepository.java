package pProject.pPro.message;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pProject.pPro.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity,Long>{
	
	// 받은 메시지 (상대방: sender)
	@Query("SELECT m FROM MessageEntity m " +
	       "JOIN FETCH m.sender s " +
	       "WHERE m.receiver.userId = :userId " +
	       "AND (:keyword IS NULL OR s.userNickName LIKE %:keyword%)")
	Page<MessageEntity> getMyMsgList(@Param("userId") Long userId,
	                                 @Param("keyword") String keyword,
	                                 Pageable page);

	// 보낸 메시지 (상대방: receiver)
	@Query("SELECT m FROM MessageEntity m " +
	       "JOIN FETCH m.receiver r " +
	       "WHERE m.sender.userId = :userId " +
	       "AND (:keyword IS NULL OR r.userNickName LIKE %:keyword%)")
	Page<MessageEntity> getMySendMsgList(@Param("userId") Long userId,
	                                     @Param("keyword") String keyword,
	                                     Pageable page);

	@Query("select m from MessageEntity m join fetch m.sender s join fetch m.receiver r where m.messageId =:messageId")
	Optional<MessageEntity> messageDetail(@Param("messageId")Long messageId);
	
	@Query("select count(m) from MessageEntity m where m.isRead =false and m.receiver.userId=:userId")
	int notReadMsgCount(@Param("userId")Long userId);
}
