package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.entity.Page;

public interface PageCommandService {
    Page createPage(Long projectId, PageRequest.createPage createPage);

    void deletePage(Long pageId);
}
