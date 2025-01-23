package qastudio.backend.domain.user.service;

import org.springframework.data.domain.Page;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;

import java.time.LocalDate;

public interface UserQueryService {
    User getUser(Long userId);

    Integer getProjectCount(Long userId);

    User updateUser(Long userId, UserRequest.UpdateUserInfo updateUserInfo);

    Page<UserProject> getUserProjectList(Long userId, Integer page);
}
