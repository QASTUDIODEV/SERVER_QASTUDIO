package qastudio.backend.domain.test.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

public interface TestRepositoryCustom {
    Page<Test> findAllByProject(Project project, String testName, LocalDate date, String pageName, State state, PageRequest pageRequest);

    Long countByTestDateAndState(Long projectId, LocalDate testDate, State state);
}
