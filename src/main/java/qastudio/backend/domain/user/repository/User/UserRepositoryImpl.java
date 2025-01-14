package qastudio.backend.domain.user.repository.User;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.user.entity.QAccountTable;
import qastudio.backend.domain.user.entity.QUser;
import qastudio.backend.domain.user.entity.User;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final QUser qUser = QUser.user;
    private final QAccountTable qAccountTable = QAccountTable.accountTable;

    @Override
    public Optional<User> findByEmail(String email) {
        User user = jpaQueryFactory
                .selectFrom(qUser)
                .join(qUser.accounts, qAccountTable)
                .where(qAccountTable.email.eq(email))
                .fetchOne();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        User user = jpaQueryFactory
                .selectFrom(qUser)
                .where(qUser.id.eq(userId))
                .fetchOne();
        return Optional.ofNullable(user);
    }
}
