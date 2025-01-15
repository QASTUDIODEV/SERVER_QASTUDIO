package qastudio.backend.domain.user.repository.AccountTable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.user.entity.AccountTable;
import static qastudio.backend.domain.user.entity.QAccountTable.accountTable;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccountTableRepositoryImpl implements AccountTableRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByUserIdAndEmail(Long userId, String email) {
        return jpaQueryFactory
                .selectOne()
                .from(accountTable)
                .where(
                        accountTable.user.id.eq(userId) // userId와 매핑된 AccountTable
                                .and(accountTable.email.eq(email)) // 이메일 확인
                )
                .fetchFirst() != null;
    }

    @Override
    public List<AccountTable> findByEmail(String email) {
        return jpaQueryFactory
                .selectFrom(accountTable)
                .where(accountTable.email.startsWithIgnoreCase(email)) // 이메일 검색 조건 (부분 매칭 허용)
                .fetch();
    }

}
