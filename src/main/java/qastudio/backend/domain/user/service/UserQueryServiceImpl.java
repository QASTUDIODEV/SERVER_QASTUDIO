package qastudio.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    @Override
    public UserResponse.User getUser(Long userId)  {
        return null;
    };

    @Override
    public UserResponse.User updateUser(Long userId, UserRequest.UpdateUserInfo updateUserInfo) {
        return null;
    }

    @Override
    public UserResponse.UserProjectList getUserProjectList(Long userId, Integer page) {
        return null;
    };
}
