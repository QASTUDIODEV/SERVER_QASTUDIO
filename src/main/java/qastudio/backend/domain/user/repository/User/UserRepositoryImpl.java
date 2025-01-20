package qastudio.backend.domain.user.repository.User;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
        BooleanExpression condition = userProject.user.eq(user);

        Expression<LocalDate> lastModifiedDateExpression = JPAExpressions
                .select(test.testDate.max())
                .from(test)
                .where(test.project.eq(userProject.project));

        List<UserProject> userProjects = jpaQueryFactory
                .select(userProject)
                .from(userProject)
                .where(condition)
                .orderBy(com.querydsl.core.types.dsl.Expressions.asComparable(lastModifiedDateExpression).desc()) // 정렬
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long totalCount = jpaQueryFactory
                .select(userProject.count())
                .from(userProject)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(userProjects, pageRequest, totalCount);
    }

}
