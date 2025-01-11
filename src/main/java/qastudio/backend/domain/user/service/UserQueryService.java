package qastudio.backend.domain.user.service;

import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;

public interface UserQueryService {
    UserResponse.User getUser(Long userId);

    UserResponse.User updateUser(Long userId, UserRequest.UpdateUserInfo updateUserInfo);

    UserResponse.UserProjectList getUserProjectList(Long userId, Integer page);
}
