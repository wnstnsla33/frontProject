package pProject.pPro.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>{
	@Query("select p from PostEntity p where p.postId=:postId")
	void incrementViewCount(@Param("postId")Long PostId);		
	
}

