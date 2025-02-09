package qastudio.backend.domain.test.repository;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TestRepositoryCustom {
    Page<Test> findAllByProject(Project project, String testName, LocalDate date, String pageName, State state, PageRequest pageRequest);

    List<Tuple> countTestsByProject(Long projectId);
}