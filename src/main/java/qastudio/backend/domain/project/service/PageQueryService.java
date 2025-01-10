package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;

public interface PageQueryService {
    PageResponse.PageSummary createPage(Long projectId, PageRequest.@Valid createPage createPage);

    PageResponse.PageSummary deletePage(Long pageId);
}
