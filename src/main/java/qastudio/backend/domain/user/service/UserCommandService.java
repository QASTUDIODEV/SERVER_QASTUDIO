package qastudio.backend.domain.user.service;

import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.entity.User;

public interface UserCommandService {
    void createProfile(Long userId, UserRequest.CreateUserInfo userInfo);
    User updateProfile(Long userId, UserRequest.UpdateUserInfo userInfo);
}
