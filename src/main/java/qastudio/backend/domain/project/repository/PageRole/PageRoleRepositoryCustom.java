package qastudio.backend.domain.project.repository.PageRole;

import qastudio.backend.domain.project.entity.PageRole;

import java.util.List;

public interface PageRoleRepositoryCustom {
    List<PageRole> findAllByPageId(Long pageId);
}
