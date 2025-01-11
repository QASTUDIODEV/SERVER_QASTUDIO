package qastudio.backend.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.test.dto.response.TestResponse;

@Service
@RequiredArgsConstructor
public class TestQueryServiceImpl implements TestQueryService{
    @Override
    public TestResponse.TestStatistics getTestStatistics(Long projectId) {
        return null;
    }
}
