package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;

public interface PageCommandService {
    PageResponse.PageSummary createPage(Long projectId, PageRequest.createPage createPage);

    void deletePage(Long pageId);
}
