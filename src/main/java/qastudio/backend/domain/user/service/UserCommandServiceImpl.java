package qastudio.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.converter.UserConverter;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.s3.service.S3Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final S3Service s3Service;

    @Override
    public UserResponse.UserProfile createProfile(Long userId, UserRequest.CreateUserInfo userInfo) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        String profileImageUrl = null;
        if (userInfo.getProfileImage() != null) {
            profileImageUrl = s3Service.generateStaticUrl(userInfo.getProfileImage());
        }

        user.updateProfile(userInfo.getNickname(), profileImageUrl);

        return userConverter.toUserProfile(user);
    }

    @Override
    public User updateProfile(Long userId, UserRequest.UpdateUserInfo userInfo) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        String profileImageUrl = null;
        if (userInfo.getProfileImage() != null) {
            profileImageUrl = s3Service.generateStaticUrl(userInfo.getProfileImage());
        }

        String bannerImageUrl = null;
        if (userInfo.getBannerImage() != null) {
            bannerImageUrl = s3Service.generateStaticUrl(userInfo.getBannerImage());
        }

        user.updateUserInfo(userInfo.getNickname(), profileImageUrl, bannerImageUrl);

        return user;
    }
}
