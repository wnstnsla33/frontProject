package pProject.pPro.bookmark;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.post.DTO.PostListDTO;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {
	@Modifying // 북마크 삭제
	@Query("DELETE FROM BookmarkEntity b WHERE b.post.postId = :postId AND b.user.userEmail = :userEmail")
	void deleteBookmark(@Param("postId") Long postId, @Param("userEmail") String userEmail);


	// 로그인한 계정이 북마크한 postList
	@Query("""
			    SELECT new pProject.pPro.post.DTO.PostListDTO(b.post,true)
			    FROM BookmarkEntity b
			    WHERE b.user.userEmail = :userEmail
			""")
	Page<PostListDTO> bookmarkListByUser(@Param("userEmail") String userEmail, Pageable pageable);

	// 계정이 북마크한지 안한지
	@Query("select b FROM BookmarkEntity b WHERE b.post.postId = :postId AND b.user.userEmail = :userEmail")
	Optional<BookmarkEntity> findBookmark(@Param("postId") Long postId, @Param("userEmail") String userEmail);

	@Query("select count(*) from BookmarkEntity b where b.user.userEmail =:userEmail")
	Long countMyBookmark(@Param("userEmail") String email);
}
