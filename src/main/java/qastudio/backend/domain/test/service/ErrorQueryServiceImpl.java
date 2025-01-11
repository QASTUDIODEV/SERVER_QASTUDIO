package qastudio.backend.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.test.dto.response.ErrorResponse;

@Service
@RequiredArgsConstructor
public class ErrorQueryServiceImpl implements ErrorQueryService {
    @Override
    public ErrorResponse.Error getError(Long testId) {
        return null;
    }
}
