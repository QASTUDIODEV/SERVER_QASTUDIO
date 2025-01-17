package qastudio.backend.domain.project.repository.Page;

import qastudio.backend.domain.project.entity.Page;

import java.util.List;
import java.util.Optional;

public interface PageRepositoryCustom {
    List<Page> findAllByProjectId(Long projectId);
    Optional<Page> findByPageId(Long pageId);
}
