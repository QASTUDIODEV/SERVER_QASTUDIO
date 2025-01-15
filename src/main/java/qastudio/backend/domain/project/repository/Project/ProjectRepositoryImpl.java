package qastudio.backend.domain.project.repository.Project;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.Project;
import static qastudio.backend.domain.project.entity.QProject.project;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Project> findByProjectId(Long projectId) {
        Project resultProject = jpaQueryFactory
                .selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne();
        return Optional.ofNullable(resultProject);
    }
}
