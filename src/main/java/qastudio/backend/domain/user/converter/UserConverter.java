package qastudio.backend.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.test.entity.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverter {

    public static UserResponse.UserProfile toUserProfile(User user) {
        return UserResponse.UserProfile.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

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

    public static UserResponse.MemberInfo toMemberInfo(User user, Integer projectCnt) {
        return UserResponse.MemberInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getAccounts().get(0).getEmail())
                .profileImage(user.getProfileImage())
                .bannerImage(user.getBannerImage())
                .projectCnt(projectCnt)
                .build();
    }

    public static UserResponse.UserProject toUserProject(UserProject userProject) {
        Integer participantCnt = userProject.getProject().getUserProjects().size();

        LocalDate lastModifiedDate = userProject.getProject().getTests().stream()
                .map(Test::getTestDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        return UserResponse.UserProject.builder()
                .projectId(userProject.getProject().getId())
                .projectName(userProject.getProject().getProjectName())
                .projectImage(userProject.getProject().getProjectImage())
                .participant(participantCnt)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }

    public static UserResponse.UserProjectList toUserProjectList(Page<UserProject> userProjectList) {
        List<UserResponse.UserProject> userProjectLists = userProjectList.stream()
                .map(UserConverter::toUserProject).collect(Collectors.toList());

        return UserResponse.UserProjectList.builder()
                .userProjectList(userProjectLists)
                .listSize(userProjectLists.size())
                .totalPage(userProjectList.getTotalPages())
                .totalElements(userProjectList.getTotalElements())
                .isFirst(userProjectList.isFirst())
                .isLast(userProjectList.isLast())
                .build();
    }
}
