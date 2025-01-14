package qastudio.backend.domain.project.repository.Page;

import qastudio.backend.domain.project.entity.Page;

import java.util.List;

public interface PageRepositoryCustom {
    List<Page> findAllByProjectId(Long projectId);
}
