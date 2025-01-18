package qastudio.backend.domain.user.service;

import qastudio.backend.domain.user.dto.request.UserRequest;

public interface UserCommandService {
    void updateProfile(Long userId, UserRequest.UpdateUserInfo userInfo);
}
