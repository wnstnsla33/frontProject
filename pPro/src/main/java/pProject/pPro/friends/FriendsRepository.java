package pProject.pPro.friends;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pProject.pPro.entity.FriendsEntity;

public interface FriendsRepository extends JpaRepository<FriendsEntity, Long>{
	//나의 친구요청 리스트
	@Query("SELECT f FROM FriendsEntity f join fetch f.my m WHERE f.type = 'REQUEST' AND f.friend.userId = :userId")
	Page<FriendsEntity> requestList(@Param("userId") Long userId, Pageable pageable);
	//친구 리스트
	@Query("SELECT f FROM FriendsEntity f join fetch f.friend ff WHERE f.type = 'ACCEPT' AND f.my.userId = :userId")
	List<FriendsEntity> friendsList(@Param("userId") Long userId);
	//친구 중복 확인
	@Query("select count(f) from FriendsEntity f where f.my.userId = :userId and f.friend.userId = :friendId and f.type = 'ACCEPT'")
	int findRequest (@Param("userId") Long userId, @Param("friendId") Long friendId);

	@Query("select count(f) from FriendsEntity f where f.my.userId = :userId and f.friend.userId = :friendId and f.type ='REQUEST'")
	int duplicateRequest (@Param("userId") Long userId, @Param("friendId") Long friendId);

	
	@Query("select count(f) from FriendsEntity f where f.friend.userId =:userId and f.type ='REQUEST'")
	int requestFriendsCount(@Param("userId")Long userId);
	
	
}
