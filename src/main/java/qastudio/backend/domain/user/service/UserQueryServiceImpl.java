package qastudio.backend.domain.user.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId)  {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
    };

    @Override
    public Integer getProjectCount(Long userId) {
        return userRepository.countProjectsByUserId(userId);
    }

    @Override
    public User updateUser(Long userId, UserRequest.UpdateUserInfo updateUserInfo) {
        return null;
    }

    @Override
    public UserResponse.UserProjectList getUserProjectList(Long userId, Integer page) {
        return null;
    };
}
