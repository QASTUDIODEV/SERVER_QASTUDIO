package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.user.entity.AccountTable;

import java.util.List;

public interface TeamMemberQueryService {
    List<TeamMemberResponse.Member> inviteMembers(Long projectId, TeamMemberRequest.@Valid Invite inviteMembers);

    List<TeamMemberResponse.Member> getTeamMemberList(Long projectId);

    void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember);

    List<AccountTable> searchMember(Long projectId, String email);
}
