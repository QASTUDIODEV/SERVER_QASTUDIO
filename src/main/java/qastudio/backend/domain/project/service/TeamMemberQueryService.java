package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface TeamMemberQueryService {
    List<UserProject> getTeamMemberList(Long projectId);

    boolean searchMember(Long projectId, String email);

    List<UserProject> getTeamMemberExceptLeader(Long projectId);

    void inviteMembers(Long projectId, List<TeamMemberRequest.MemberEmail> memberEmailList);
}
