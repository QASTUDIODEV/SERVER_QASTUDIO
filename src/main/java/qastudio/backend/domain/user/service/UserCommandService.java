package qastudio.backend.domain.user.service;

import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.entity.User;

public interface UserCommandService {
    User updateProfile(Long userId, UserRequest.UpdateUserInfo userInfo);
}
