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
public class PageQueryServiceImpl implements PageQueryService{

    private final ProjectRepository projectRepository;
    private final PageRepository pageRepository;
    private final PageScenarioRepository pageScenarioRepository;
    private final PageRoleRepository pageRoleRepository;
    private final CharacterTableRepository characterTableRepository;

    @Override
    @Transactional
    public PageResponse.PageSummary createPage(Long projectId, PageRequest.createPage createPage) {

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

        Page createdPage = pageRepository.save(page);

        // PageRole 생성
        List<Long> allowedCharacterIds = new ArrayList<>();
        List<String> allowedCharacterNames = new ArrayList<>();

        if (createPage.getCharacterIdList() != null && !createPage.getCharacterIdList().isEmpty()) {
            List<PageRole> pageRoles = createPage.getCharacterIdList().stream()
                    .map(characterId -> {
                        CharacterTable characterTable = characterTableRepository.findById(characterId)
                                .orElseThrow(() -> new BadRequestException(ErrorStatus.CHARACTER_NOT_FOUND));

                        // projectId에 해당하는 character가 아니라면 에러 응답
                        if (!Objects.equals(characterTable.getProject().getId(), projectId)) {
                            throw new CharacterException(ErrorStatus.CHARACTER_NOT_IN_PROJECT);
                        }

                        // 권한이 있는 역할 id, name 리스트 저장
                        allowedCharacterIds.add(characterId);
                        allowedCharacterNames.add(characterTable.getCharacterName());

                        return PageRole.builder()
                                .characterTable(characterTable)
                                .page(createdPage)
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
                            .page(createdPage)
                            .build())
                    .toList();

            pageScenarioRepository.saveAll(scenarios);
        }

        // 프로젝트에 속한 모든 권한 조회
        List<CharacterTable> projectRoles = characterTableRepository.findAllByProjectId(projectId);

        // 접근 권한이 없는 CharacterTable 객체 조회
        List<String> deniedAccessNames = projectRoles.stream()
                .filter(characterTable -> !allowedCharacterIds.contains(characterTable.getId()))
                .map(CharacterTable::getCharacterName)
                .toList();

        // dto 응답
        return PageResponse.PageSummary.builder()
                .pageId(createdPage.getId())
                .pageName(createdPage.getPageName())
                .pageDescription(createdPage.getPageDescription())
                .path(createdPage.getPath())
                .scenarios(createPage.getScenarioList())
                .hasAccess(allowedCharacterNames)
                .deniedAccess(deniedAccessNames)
                .build();
    }

    @Override
    @Transactional
    public void deletePage(Long pageId) {

        // 페이지 조회
        Page page = pageRepository.findByPageId(pageId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PAGE_NOT_FOUND));

        // 페이지 권한 조회
        List<PageRole> pageRoleList = pageRoleRepository.findAllByPageId(pageId);
        pageRoleRepository.deleteAll(pageRoleList);

        // 페이지 시나리오 조회
        List<PageScenario> pageScenarioList = pageScenarioRepository.findAllByPageId(pageId);
        pageScenarioRepository.deleteAll(pageScenarioList);

        pageRepository.delete(page);
    }

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
