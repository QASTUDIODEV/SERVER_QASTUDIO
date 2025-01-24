package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface TeamMemberCommandService {
    List<UserProject> inviteMembers(Long projectId, List<TeamMemberRequest.MemberEmail> memberEmailList);

    void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember);

}
