package qastudio.backend.domain.project.repository.Page;

import qastudio.backend.domain.project.entity.Page;

import java.util.List;
import java.util.Optional;

public interface PageRepositoryCustom {
    List<Page> findAllByProjectId(Long projectId);
    List<Page> findAllByProjectIdWithFetchJoin(Long projectId);
    Optional<Page> findByPageId(Long pageId);
    List<Page> findAllByPaths(String path, Long projectId);
    Optional<Page> findByPath(String path, Long projectId);
}
