package pProject.pPro.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.post.DTO.PostListDTO;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

	@Modifying
	@Query("select p from PostEntity p where p.postId=:postId")
	void incrementViewCount(@Param("postId") Long postId);

	@Query("select p from PostEntity p where p.postId=:postId")
	Optional<PostEntity> getPostDetail(@Param("postId") Long postId);

	@Query("""
		    SELECT new pProject.pPro.post.DTO.PostListDTO(
		        p,
		        CASE WHEN b.id IS NOT NULL THEN true ELSE false END
		    )
		    FROM PostEntity p
		    LEFT JOIN BookmarkEntity b ON b.post.postId = p.postId AND b.user.userId = :userId
		    WHERE p.user.userId = :userId
		""")
		List<PostListDTO> findPostsWithBookmarkInfo(
		    @Param("userId") Long userId
		);


	@Query("SELECT p FROM PostEntity p join fetch p.user WHERE p.user.userGrade = 'ADMIN'")
	List<PostEntity> getNoticeList();

	@Query("""
		    SELECT new pProject.pPro.post.DTO.PostListDTO(
	        p,
	        CASE WHEN b.id IS NOT NULL THEN true ELSE false END
	    )
	    FROM PostEntity p
	    LEFT JOIN BookmarkEntity b ON b.post.id = p.id AND b.user.id = :userId
	    where p.user.userGrade="ADMIN"
	    """)
	List<PostListDTO> getNoticeList(@Param("userId")Long userId);

	@Query("""
			    SELECT new pProject.pPro.post.DTO.PostListDTO(
			        p,
			        CASE WHEN b.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM PostEntity p
			    LEFT JOIN BookmarkEntity b ON b.post.id = p.id AND b.user.id = :userId
			    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	Page<PostListDTO> findPostsWithBookmarkInfo(@Param("userId") Long userId, @Param("keyword") String keyword,
			Pageable pageable);

	@Query("""
			    SELECT p FROM PostEntity p
			    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	Page<PostEntity> findPosts(@Param("keyword") String keyword, Pageable pageable);
	
	@Query("""
		    SELECT new pProject.pPro.post.DTO.PostListDTO(
		        p,
		        CASE WHEN b.id IS NOT NULL THEN true ELSE false END
		    )
		    FROM PostEntity p
		    JOIN FETCH p.user u
		    LEFT JOIN BookmarkEntity b ON b.post.id = p.id AND b.user.id = :userId
		    ORDER BY p.viewCount DESC
		""")
		List<PostListDTO> findTop10ByViewCount(@Param("userId") Long userId,Pageable pageable);

	@Query("""
		    SELECT new pProject.pPro.post.DTO.PostListDTO(p, false)
		    FROM PostEntity p
		    JOIN FETCH p.user u
		    ORDER BY p.viewCount DESC
		""")
		List<PostListDTO> findTop10ByViewCountFixed(Pageable pageable );

	@EntityGraph(attributePaths = {"user"})
	@Query("""
		    SELECT p FROM PostEntity p
		    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
		       OR LOWER(p.user.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
		""")
		Page<PostEntity> searchPostsByAdmin(@Param("keyword") String keyword, Pageable pageable);

	@Query("select count(p) from PostEntity p where p.user.userId =:userId")
	Long postCount(@Param("userId")Long userId);
	
	@Query("select p from PostEntity p join fetch p.user where p.postId=:postId")
	Optional<PostEntity> getPostEditInfo(@Param("postId")Long postId);
}
