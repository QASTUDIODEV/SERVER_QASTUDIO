package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class TeamMemberConverter {

    public static TeamMemberResponse.UserEmailList toUserEmailListFromUserProjects(List<UserProject> userProjects) {

        List<TeamMemberResponse.UserEmail> members = userProjects.stream()
                .map(userProject -> {
                    User user = userProject.getUser();

                    // dto 응답 추가
                    return TeamMemberResponse.UserEmail.builder()
                            .userId(user.getId())
                            .email(userProject.getUserEmail())
                            .build();
                })
                .toList();

        return TeamMemberResponse.UserEmailList.builder()
                .userEmails(members)
                .build();

    }

    public static TeamMemberResponse.MemberList toMemberList(List<UserProject> userProjects) {

        List<TeamMemberResponse.Member> members = userProjects.stream()
                .map(userProject -> {
                    User user = userProject.getUser();

                    // dto 응답 추가
                    return TeamMemberResponse.Member.builder()
                            .userId(user.getId())
                            .projectRole(userProject.getRole())
                            .email(userProject.getUserEmail())
                            .nickname(user.getNickname())
                            .profileImage(user.getProfileImage())
                            .build();
                })
                .collect(Collectors.toList());

        return TeamMemberResponse.MemberList.builder()
                    .members(members)
                    .build();
    }

    public static TeamMemberResponse.UserEmailList toUserEmailListFromAccounts(List<AccountTable> accounts) {

        List<TeamMemberResponse.UserEmail> userEmails = accounts.stream()
                .map(account -> TeamMemberResponse.UserEmail.builder()
                        .email(account.getEmail())
                        .userId(account.getUser().getId())
                        .build())
                .collect(Collectors.toList());

        return TeamMemberResponse.UserEmailList.builder()
                .userEmails(userEmails)
                .build();
    }

    public static UserProject toUserProject(User user, Project project, Role role, String email) {
        return UserProject.builder()
                .user(user)
                .project(project)
                .role(role)
                .userEmail(email)
                .build();
    }

    public static TeamMemberResponse.AcceptInvitation toAcceptInvitation(Long projectId) {
        return TeamMemberResponse.AcceptInvitation.builder()
                .projectId(projectId)
                .build();
    }

    public static TeamMemberResponse.SearchEmail toSearchEmail(boolean isMember) {
        return TeamMemberResponse.SearchEmail.builder()
                .isQastudioMember(isMember)
                .build();
    }
}
