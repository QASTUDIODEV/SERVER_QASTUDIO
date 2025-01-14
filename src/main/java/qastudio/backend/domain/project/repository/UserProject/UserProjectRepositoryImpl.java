package qastudio.backend.domain.project.repository.UserProject;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.QProject;
import qastudio.backend.domain.project.entity.QUserProject;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.entity.QAccountTable;
import qastudio.backend.domain.user.entity.QUser;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserProjectRepositoryImpl implements UserProjectRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final QUserProject qUserProject = QUserProject.userProject;
    private final QProject qProject = QProject.project;
    private final QUser qUser = QUser.user;
    private final QAccountTable qAccountTable = QAccountTable.accountTable;

    @Override
    public boolean existsByUserIdAndProjectId(Long userId, Long projectId) {
        return jpaQueryFactory
                .selectOne()
                .from(qUserProject)
                .where(
                        qUserProject.user.id.eq(userId)
                                .and(qUserProject.project.id.eq(projectId))
                )
                .fetchFirst() != null;
    }

    @Override
    public List<UserProject> findByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(qUserProject)
                .where(qUserProject.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public List<UserProject> findByUserId(Long userId) {
        return jpaQueryFactory
                .selectFrom(qUserProject)
                .where(qUserProject.user.id.eq(userId))
                .fetch();
    }

}
