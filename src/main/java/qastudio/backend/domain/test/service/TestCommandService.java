package qastudio.backend.domain.test.service;


import qastudio.backend.domain.test.dto.request.TestRequest;

public interface TestCommandService {
    Long createTest(TestRequest testRequest);
}
