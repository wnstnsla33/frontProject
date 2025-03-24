package pProject.pPro.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
	@Modifying
	@Query("select p from PostEntity p where p.postId=:postId")
	void incrementViewCount(@Param("postId") Long postId);

	@Query("select p from PostEntity p where p.postId=:postId")
	Optional<PostEntity> getPostDetail(@Param("postId") Long postId);
	
	@Query("select p from PostEntity p where p.user.userEmail = :userEmail")
	List<PostEntity> getMyPostList(@Param("userEmail")String Email);
	
}
