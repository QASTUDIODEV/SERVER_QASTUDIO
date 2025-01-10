package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
public class PageQueryServiceImpl implements PageQueryService{
    @Override
    public PageResponse.PageSummary createPage(Long projectId, PageRequest.@Valid createPage createPage) {
        return null;
    }

    @Override
    public PageResponse.PageSummary deletePage(Long pageId) {
        return null;
    }
}
