package qastudio.backend.domain.project.repository.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.QCharacterTable;

import static qastudio.backend.domain.project.entity.QPage.page;
import static qastudio.backend.domain.project.entity.QPageScenario.pageScenario;
import static qastudio.backend.domain.project.entity.QPageRole.pageRole;
import static qastudio.backend.domain.project.entity.QCharacterTable.characterTable;
import static qastudio.backend.domain.project.entity.QProject.project;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PageRepositoryImpl implements PageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Page> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(page)
                .where(page.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public List<Page> findAllByProjectIdWithFetchJoin(Long projectId) {
        QCharacterTable pct = new QCharacterTable("pct");

        return jpaQueryFactory
                .selectFrom(page)
                .distinct()
                .leftJoin(page.pageScenarios, pageScenario).fetchJoin()
                .leftJoin(page.pageRoles, pageRole).fetchJoin()
                .leftJoin(pageRole.characterTable, characterTable).fetchJoin()
                .leftJoin(page.project, project).fetchJoin()
                .leftJoin(project.characterTables, pct).fetchJoin()
                .where(page.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public Optional<Page> findByPageId(Long pageId) {
        Page resultPage = jpaQueryFactory
                .selectFrom(page)
                .where(page.id.eq(pageId))
                .fetchOne();
        return Optional.ofNullable(resultPage);
    }

    @Override
    public List<Page> findAllByPaths(String path, Long projectId) {
        return jpaQueryFactory.selectFrom(page)
                .where(page.path.eq(path).and(page.project.id.eq(projectId))) // projectId 조건 추가
                .fetch();
    }

    @Override
    public Optional<Page> findByPath(String path, Long projectId) {
        Page resultPage = jpaQueryFactory
                .selectFrom(page)
                .where(page.path.eq(path).and(page.project.id.eq(projectId)))
                .fetchOne();
        return Optional.ofNullable(resultPage);
    }
}
