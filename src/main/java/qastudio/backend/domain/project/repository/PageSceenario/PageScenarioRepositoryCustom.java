package qastudio.backend.domain.project.repository.PageSceenario;

import qastudio.backend.domain.project.entity.PageScenario;

import java.util.List;

public interface PageScenarioRepositoryCustom {
    List<PageScenario> findAllByPageId(Long pageId);
}
