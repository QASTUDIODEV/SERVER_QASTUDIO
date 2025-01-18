package qastudio.backend.domain.user.service;

import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;

public interface UserQueryService {
    User getUser(Long userId);

    Integer getProjectCount(Long userId);

    User updateUser(Long userId, UserRequest.UpdateUserInfo updateUserInfo);

    UserResponse.UserProjectList getUserProjectList(Long userId, Integer page);
}
