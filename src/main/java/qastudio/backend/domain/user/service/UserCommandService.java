package qastudio.backend.domain.user.service;

import qastudio.backend.domain.user.dto.request.UserRequest;

public interface UserCommandService {
    void createProfile(Long userId, UserRequest.CreateUserInfo userInfo);
}
