package pProject.pPro.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
	
	@Query("SELECT u FROM UserEntity u WHERE u.userEmail = :email")
	Optional<UserEntity> findByEmail(@Param("email") String email);
	@Query("SELECT u FROM UserEntity u WHERE u.userNickName = :userNickName")
	Optional<UserEntity> findByNickname(@Param("userNickName") String userNickName);
	@Query("SELECT u FROM UserEntity u WHERE u.userName = :name")
	UserEntity findByName(@Param("name") String name);
	void deleteByUserEmail(String userEmail);
	
	Page<UserEntity> findByUserNameContainingIgnoreCase(String keyword, Pageable pageable);

	@EntityGraph(attributePaths = {"joinedRooms.room"})
	@Query("select u from UserEntity u where u.userId=:userId")
	Optional<UserEntity> getUserInfo(@Param("userId")Long userId);
	
	long countByUserNickName(String nickname);

}
