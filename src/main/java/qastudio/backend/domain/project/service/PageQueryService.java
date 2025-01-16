package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.entity.Page;

import java.util.List;

public interface PageQueryService {
    List<Page> getAllPage(Long projectId);
}
