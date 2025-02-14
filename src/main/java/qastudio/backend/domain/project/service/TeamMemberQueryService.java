package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface TeamMemberQueryService {
    TeamMemberResponse.MemberList getTeamMemberList(Long projectId);

    boolean searchMember(Long projectId, String email);

    TeamMemberResponse.UserEmailList getTeamMemberExceptLeader(Long projectId);

    void inviteMembers(Long projectId, List<TeamMemberRequest.MemberEmail> memberEmailList);
}
