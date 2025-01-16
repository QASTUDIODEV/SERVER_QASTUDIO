package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.PageScenario;

import java.util.List;

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

        List<String> scenarioContents = page.getPageScenarios().stream()
                .map(PageScenario::getContent)
                .toList();

        List<String> allowedCharacterNames = page.getPageRoles().stream()
                .map(pageRole -> pageRole.getCharacterTable().getCharacterName())
                .toList();

        List<CharacterTable> projectCharacters = page.getProject().getCharacterTables();
        List<String> deniedAccessNames = projectCharacters.stream()
                .filter(character -> page.getPageRoles().stream()
                        .noneMatch(role -> role.getCharacterTable().equals(character)))
                .map(CharacterTable::getCharacterName)
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
