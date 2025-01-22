package qastudio.backend.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.test.entity.Error;
import qastudio.backend.domain.test.repository.ErrorRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ErrorQueryServiceImpl implements ErrorQueryService {

    private final ErrorRepository errorRepository;

    @Override
    public Error getError(Long testId) {
        return errorRepository.findByTestId(testId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.ERROR_NOT_FOUND));
    }
}
