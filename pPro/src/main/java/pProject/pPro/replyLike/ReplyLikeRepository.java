package pProject.pPro.replyLike;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pProject.pPro.entity.ReplyLikeEntity;

public interface ReplyLikeRepository extends JpaRepository<ReplyLikeEntity	, Long>{
	
	@Query("select r from ReplyLikeEntity r where r.user.userId=:userId and r.reply.replyId=:replyId")
	Optional< ReplyLikeEntity> findReplyLike(@Param("userId")Long userId, @Param("replyId")Long replyId);

}
