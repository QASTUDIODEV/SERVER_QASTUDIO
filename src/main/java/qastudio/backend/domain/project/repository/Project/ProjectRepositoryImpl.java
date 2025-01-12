package qastudio.backend.domain.project.repository.Project;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.QProject;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QProject qProject = QProject.project;

    @Override
    public Optional<Project> findByProjectId(Long projectId) {
        Project project = jpaQueryFactory
                .selectFrom(qProject)
                .where(qProject.id.eq(projectId))
                .fetchOne();
        return Optional.ofNullable(project);
    }
}
