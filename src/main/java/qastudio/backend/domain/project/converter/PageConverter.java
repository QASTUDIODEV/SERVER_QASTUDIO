package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.entity.Page;

import java.util.List;

public class PageConverter {

    public static PageResponse.PageList toPageList(List<PageResponse.PageSummary> pageSummaries) {
        return PageResponse.PageList.builder()
                .pageSummaryList(pageSummaries)
                .build();
    }
}
