package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.entity.*;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageRole.PageRoleRepository;
import qastudio.backend.domain.project.repository.PageSceenario.PageScenarioRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.exception.custom.CharacterException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageQueryServiceImpl implements PageQueryService{

    private final ProjectRepository projectRepository;
    private final PageRepository pageRepository;
    private final PageScenarioRepository pageScenarioRepository;
    private final PageRoleRepository pageRoleRepository;
    private final CharacterTableRepository characterTableRepository;

    @Override
    public List<PageResponse.PageSummary> getAllPage(Long projectId) {

        // 프로젝트에 존재하는 모든 역할 조회
        List<String> allRoles = characterTableRepository.findAllByProjectId(projectId).stream()
                .map(CharacterTable::getCharacterName)
                .toList();

        // 프로젝트에 해당하는 페이지 조회
        List<Page> pageList = pageRepository.findAllByProjectId(projectId);

        return pageList.stream()
                .map(page -> {
                    // 페이지에 접근 가능한 역할 리스트
                    List<String> hasAccess = pageRoleRepository.findAllByPageId(page.getId()).stream()
                            .map(pageRole -> pageRole.getCharacterTable().getCharacterName())
                            .toList();

                    // 접근 권한이 없는 역할 리스트
                    List<String> deniedAccess = allRoles.stream()
                            .filter(role -> !hasAccess.contains(role))
                            .toList();

                    // 시나리오 조회
                    List<PageScenario> pageScenarioList = pageScenarioRepository.findAllByPageId(page.getId());
                    List<String> pageScenarioStringList = pageScenarioList.stream()
                            .map(PageScenario::getContent)
                            .toList();

                    // DTO 변환
                    return PageResponse.PageSummary.builder()
                            .pageId(page.getId())
                            .pageName(page.getPageName())
                            .pageDescription(page.getPageDescription())
                            .path(page.getPath())
                            .hasAccess(hasAccess)
                            .deniedAccess(deniedAccess)
                            .scenarios(pageScenarioStringList)
                            .build();
                })
                .toList();
    }
}
