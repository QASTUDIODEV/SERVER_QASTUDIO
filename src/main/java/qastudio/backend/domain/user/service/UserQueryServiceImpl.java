package qastudio.backend.domain.user.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.converter.UserConverter;
import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    public User getUser(Long userId)  {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
    };

    @Override
    public UserResponse.UserProfile getProfile(Long userId) {
        return userConverter.toUserProfile(
                userRepository.findByUserId(userId)
                        .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND))
        );
    }

    @Override
    public Integer getProjectCount(Long userId) {
        return userRepository.countProjectsByUserId(userId);
    }

    @Override
    public User updateUser(Long userId, UserRequest.UpdateUserInfo updateUserInfo) {
        return null;
    }

    @Override
    public Page<UserProject> getUserProjectList(Long userId, Integer page) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new AuthException(ErrorStatus.USER_NOT_FOUND));

        Page<UserProject> UserProjectPage = userRepository.findAllByUser(user, PageRequest.of(page, 7));

        return UserProjectPage;
    };
}
