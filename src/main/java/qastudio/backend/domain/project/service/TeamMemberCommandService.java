package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;

import java.util.List;

public interface TeamMemberCommandService {
    List<TeamMemberResponse.Member> inviteMembers(TeamMemberRequest.Invite inviteMembers);

    void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember);

}
