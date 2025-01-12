package qastudio.backend.domain.user.repository.AccountTable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.QAccountTable;

@Repository
@RequiredArgsConstructor
public class AccountTableRepositoryImpl implements AccountTableRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final QAccountTable qAccountTable = QAccountTable.accountTable;

    @Override
    public boolean existsByUserIdAndEmail(Long userId, String email) {
        return jpaQueryFactory
                .selectOne()
                .from(qAccountTable)
                .where(
                        qAccountTable.user.id.eq(userId) // userId와 매핑된 AccountTable
                                .and(qAccountTable.email.eq(email)) // 이메일 확인
                )
                .fetchFirst() != null;
    }

}
