package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.scenario.dto.request.ActionUpdateRequest;
import qastudio.backend.domain.scenario.dto.response.ActionResponse;

@Service
@RequiredArgsConstructor
public class ActionCommandServiceImpl implements ActionCommandService {

    @Override
    @Transactional
    public ActionResponse updateAction(Long actionId, ActionUpdateRequest request) {
        return null;
    }
}