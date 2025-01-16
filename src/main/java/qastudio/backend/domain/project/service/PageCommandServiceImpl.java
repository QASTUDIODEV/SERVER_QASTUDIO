package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.entity.*;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageRole.PageRoleRepository;
import qastudio.backend.domain.project.repository.PageSceenario.PageScenarioRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.exception.custom.CharacterException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PageCommandServiceImpl implements PageCommandService{

    private final ProjectRepository projectRepository;
    private final PageRepository pageRepository;
    private final PageScenarioRepository pageScenarioRepository;
    private final PageRoleRepository pageRoleRepository;
    private final CharacterTableRepository characterTableRepository;

    @Override
    public Page createPage(Long projectId, PageRequest.createPage createPage) {

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 페이지 생성
        Page page = Page.builder()
                .pageName(createPage.getPageName())
                .pageDescription(createPage.getPageDescription())
                .path(createPage.getPath())
                .project(project)
                .build();
        Page newPage = pageRepository.save(page);

        // 페이지 역할 생성
        if (createPage.getCharacterIdList() != null && !createPage.getCharacterIdList().isEmpty()) {
            List<PageRole> pageRoles = createPage.getCharacterIdList().stream()
                    .map(characterId -> {
                        CharacterTable characterTable = characterTableRepository.findById(characterId)
                                .orElseThrow(() -> new BadRequestException(ErrorStatus.CHARACTER_NOT_FOUND));

                        // projectId에 해당하는 character가 아니라면 에러 응답
                        if (!Objects.equals(characterTable.getProject().getId(), projectId)) {
                            throw new CharacterException(ErrorStatus.CHARACTER_NOT_IN_PROJECT);
                        }

                        return PageRole.builder()
                                .characterTable(characterTable)
                                .page(newPage)
                                .build();
                    })
                    .toList();

            pageRoleRepository.saveAll(pageRoles);
        }

        // 시나리오 생성
        if (createPage.getScenarioList() != null && !createPage.getScenarioList().isEmpty()) {
            List<PageScenario> scenarios = createPage.getScenarioList().stream()
                    .map(scenarioContent -> PageScenario.builder()
                            .content(scenarioContent)
                            .page(newPage)
                            .build())
                    .toList();

            pageScenarioRepository.saveAll(scenarios);
        }

        return newPage;
    }

    @Override
    public void deletePage(Long pageId) {

        // 페이지 조회
        Page page = pageRepository.findByPageId(pageId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PAGE_NOT_FOUND));

        pageRepository.delete(page);
    }
}
