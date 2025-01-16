package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.entity.Page;

import java.util.List;

public interface PageQueryService {
    List<PageResponse.PageSummary> getAllPage(Long projectId);
}
