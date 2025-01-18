package qastudio.backend.domain.user.repository.User;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import static qastudio.backend.domain.project.entity.QUserProject.userProject;
import static qastudio.backend.domain.test.entity.QTest.test;
import static  qastudio.backend.domain.user.entity.QAccountTable.accountTable;
import static  qastudio.backend.domain.user.entity.QUser.user;

import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
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

    @Override
    public Page<UserProject> findAllByUser(User user, PageRequest pageRequest) {
        // 조건문 생성
        BooleanExpression condition = userProject.user.eq(user);

        // 페이징 처리된 데이터 조회
        List<UserProject> userProjects = jpaQueryFactory
                .selectFrom(userProject)
                .where(condition)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        // 총 개수 조회
        long totalCount = jpaQueryFactory
                .select(userProject.count())
                .from(userProject)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(userProjects, pageRequest, totalCount);
    }
}
