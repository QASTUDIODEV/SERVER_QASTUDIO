package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.PageRequest;

public interface PageCommandService {
    void createPage(Long projectId, PageRequest.createPage createPage);

    void deletePage(Long pageId);
}
