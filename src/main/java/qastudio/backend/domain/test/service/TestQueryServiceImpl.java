package qastudio.backend.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TestQueryServiceImpl implements TestQueryService{
    @Override
    public TestResponse.TestList getTestList(Long projectId, Integer page, LocalDate date, String pageName, State state) {
        return null;
    };

    @Override
    public TestResponse.TestStatistics getTestStatistics(Long projectId) {
        return null;
    }

    @Override
    public TestResponse.TestList searchTestsByTestName(Long projectId, String testName, Integer page, LocalDate date, String pageName, State state) {
        return null;
    };
}
