package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PageConverter {

    public static PageResponse.PageSummary toPageSummary(Page page) {

        return PageResponse.PageSummary.builder()
                .pageId(page.getId())
                .pageName(page.getPageName())
                .pageDescription(page.getPageDescription())
                .path(page.getPath())
                .build();
    }

    public static PageResponse.PageDetail toPageDetail(Page page) {
        // 페이지의 시나리오 리스트 조회
        List<String> scenarioContents = Optional.ofNullable(page.getPageScenarios())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(PageScenario::getContent)
                .toList();

        // 페이지에 접근 가능한 역할 이름 조회
        List<String> allowedCharacterNames = Optional.ofNullable(page.getPageRoles())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(PageRole::getCharacterTable)
                        .map(CharacterTable::getCharacterName)
                .distinct()
                .toList();

        // 페이지에 접근 불가능한 역할 이름 조회
        List<String> deniedAccessNames = Optional.ofNullable(page.getProject())
                .map(Project::getCharacterTables)
                .orElse(Collections.emptyList())
                .stream()
                .filter(character -> Optional.ofNullable(page.getPageRoles())// 프로젝트의 전체 역할 조회, 없다면 빈 리스트 응답
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(Objects::nonNull)
                        .noneMatch(role -> role.getCharacterTable() != null &&
                                role.getCharacterTable().equals(character))) // 역할이 존재하지 않으면 필터링해서 가져옴
                .map(CharacterTable::getCharacterName)
                .distinct()
                .toList();

        return PageResponse.PageDetail.builder()
                .pageId(page.getId())
                .pageName(page.getPageName())
                .pageDescription(page.getPageDescription())
                .path(page.getPath())
                .scenarios(scenarioContents)
                .hasAccess(allowedCharacterNames)
                .deniedAccess(deniedAccessNames)
                .build();
    }



    public static PageResponse.PageList toPageList(List<Page> pages) {

        List<PageResponse.PageDetail> pageSummaries = pages.stream()
                .map(PageConverter::toPageDetail)
                .toList();

        return PageResponse.PageList.builder()
                .pageSummaryList(pageSummaries)
                .build();
    }
}
