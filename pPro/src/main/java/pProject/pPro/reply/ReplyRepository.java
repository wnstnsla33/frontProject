package pProject.pPro.reply;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.reply.DTO.ReplyListDTO;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long>{
	@Query("""
		    SELECT new pProject.pPro.reply.DTO.ReplyListDTO(
		        r.replyId,
		        CASE WHEN r.parent IS NOT NULL THEN r.parent.replyId ELSE null END,
		        u.userId,
		        r.content,
		        u.userNickName,
		        u.userImg,
		        r.createDate,
		        r.modifiedDate,
		        r.post.postId,
		        CASE WHEN rl.id IS NOT NULL THEN true ELSE false END,
		        r.likeCount
		    )
		    FROM ReplyEntity r
		    JOIN r.user u
		    LEFT JOIN ReplyLikeEntity rl ON rl.reply.replyId = r.replyId AND rl.user.userId = :loginUserId
		    WHERE r.post.postId = :postId
		    ORDER BY r.parent.replyId NULLS FIRST, r.createDate ASC
		""")
		List<ReplyListDTO> findReplyDTOByPostLogin(@Param("postId") Long postId, @Param("loginUserId") Long loginUserId);

	@Query("""
		    SELECT new pProject.pPro.reply.DTO.ReplyListDTO(
		        r.replyId,
		        CASE WHEN r.parent IS NOT NULL THEN r.parent.replyId ELSE null END,
		        u.userId,
		        r.content,
		        u.userNickName,
		        u.userImg,
		        r.createDate,
		        r.modifiedDate,
		        r.post.postId,
		        false,                     
		        r.likeCount
		    )
		    FROM ReplyEntity r
		    JOIN r.user u
		    WHERE r.post.postId = :postId
		    ORDER BY r.parent.replyId NULLS FIRST, r.createDate ASC
		""")
		List<ReplyListDTO> findReplyDTOByPost(@Param("postId") Long postId);

	
	@Query("select count(r) from ReplyEntity r where r.user.userId=:userId")
	Long replyCount(@Param("userId")Long userId);
	
}
