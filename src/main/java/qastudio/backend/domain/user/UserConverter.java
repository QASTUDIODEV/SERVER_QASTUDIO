package qastudio.backend.domain.user;

import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.AccountTable;

public class UserConverter {

    public static UserResponse.UserInfo toUserInfo(User user, Integer projectCnt) {
        return UserResponse.UserInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getAccounts().get(0).getEmail())
                .profileImage(user.getProfileImage())
                .bannerImage(user.getBannerImage())
                .account(user.getAccounts().stream()
                        .map(AccountTable::getEmailType)
                        .toList())
                .projectCnt(projectCnt)
                .build();
    }
}
