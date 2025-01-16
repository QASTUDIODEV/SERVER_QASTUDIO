package qastudio.backend.domain.project.repository.UserProject;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static qastudio.backend.domain.project.entity.QProject.project;
import static qastudio.backend.domain.project.entity.QUserProject.userProject;
import qastudio.backend.domain.project.entity.UserProject;
import static qastudio.backend.domain.user.entity.QAccountTable.accountTable;
import static qastudio.backend.domain.user.entity.QUser.user;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserProjectRepositoryImpl implements UserProjectRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByUserIdAndProjectId(Long userId, Long projectId) {
        return jpaQueryFactory
                .selectOne()
                .from(userProject)
                .where(
                        userProject.user.id.eq(userId)
                                .and(userProject.project.id.eq(projectId))
                )
                .fetchFirst() != null;
    }

    @Override
    public List<UserProject> findByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(userProject)
                .where(userProject.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public List<UserProject> findByUserId(Long userId) {
        return jpaQueryFactory
                .selectFrom(userProject)
                .where(userProject.user.id.eq(userId))
                .fetch();
    }

}
