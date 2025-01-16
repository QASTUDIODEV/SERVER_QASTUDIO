package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.entity.*;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageQueryServiceImpl implements PageQueryService{

    private final PageRepository pageRepository;
    private final CharacterTableRepository characterTableRepository;

    @Override
    public List<Page> getAllPage(Long projectId) {

        // 프로젝트에 해당하는 페이지 조회
        return pageRepository.findAllByProjectId(projectId);
    }
}
