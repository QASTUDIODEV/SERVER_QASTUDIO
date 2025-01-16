package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.response.PageResponse;

import java.util.List;

public interface PageQueryService {
    List<PageResponse.PageSummary> getAllPage(Long projectId);
}
