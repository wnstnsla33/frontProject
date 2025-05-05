package pProject.pPro.User;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import pProject.pPro.entity.QUserEntity;
import pProject.pPro.entity.UserEntity;

@Repository
@RequiredArgsConstructor
public class UserQRepository {

    private final JPAQueryFactory queryFactory;

    // ✅ Q 객체 인스턴스 생성
    private final QUserEntity user = QUserEntity.userEntity;

    public List<UserEntity> searchUsers(String keyword) {
        return queryFactory
                .selectFrom(user)
                .where(
                        user.userEmail.containsIgnoreCase(keyword)
//                        user.userAge.goe(20)
                )
                .orderBy(user.recentLoginTime.desc())
                .fetch();
    }
}
