package pProject.pPro.reply;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.ReplyEntity;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long>{
	@Query("select r from ReplyEntity r where r.post.postId = :postId and r.parent IS NULL")
	List<ReplyEntity> findReplyByPost(@Param("postId") Long postId, Sort sort);

}
