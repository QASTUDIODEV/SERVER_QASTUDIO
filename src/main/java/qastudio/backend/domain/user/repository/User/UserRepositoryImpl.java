package qastudio.backend.domain.user.repository.User;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static qastudio.backend.domain.project.entity.QUserProject.userProject;
import static  qastudio.backend.domain.user.entity.QAccountTable.accountTable;
import static  qastudio.backend.domain.user.entity.QUser.user;
import qastudio.backend.domain.user.entity.User;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findByEmail(String email) {
        User resultUser = jpaQueryFactory
                .selectFrom(user)
                .join(user.accounts, accountTable)
                .where(accountTable.email.eq(email))
                .fetchOne();
        return Optional.ofNullable(resultUser);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        User resultUser = jpaQueryFactory
                .selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne();
        return Optional.ofNullable(resultUser);
    }

    @Override
    public Integer countProjectsByUserId(Long userId) {
        Long projectCount = jpaQueryFactory
                .select(userProject.count())
                .from(userProject)
                .where(userProject.user.id.eq(userId))
                .fetchOne();
        return projectCount != null ? projectCount.intValue() : 0;
    }
}
