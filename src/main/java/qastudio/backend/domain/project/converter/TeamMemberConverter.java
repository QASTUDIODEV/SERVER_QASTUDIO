package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.user.entity.AccountTable;

import java.util.List;
import java.util.stream.Collectors;

public class TeamMemberConverter {

    public static TeamMemberResponse.MemberList toMemberList(List<TeamMemberResponse.Member> members) {
        return TeamMemberResponse.MemberList.builder()
                    .members(members)
                    .build();
    }

    public static TeamMemberResponse.UserEmailList toUserEmailList(List<AccountTable> accounts) {

        List<TeamMemberResponse.UserEmail> userEmails = accounts.stream()
                .map(account -> TeamMemberResponse.UserEmail.builder()
                        .email(account.getEmail())
                        .build())
                .collect(Collectors.toList());

        return TeamMemberResponse.UserEmailList.builder()
                .userEmails(userEmails)
                .build();
    }
}
