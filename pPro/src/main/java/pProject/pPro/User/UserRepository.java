package pProject.pPro.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
	
	@Query("SELECT u FROM UserEntity u WHERE u.userEmail = :email")
	UserEntity findByEmail(@Param("email") String email);

	@Query("SELECT u FROM UserEntity u WHERE u.userName = :name")
	UserEntity findByName(@Param("name") String name);
	void deleteByUserEmail(String userEmail);
}
