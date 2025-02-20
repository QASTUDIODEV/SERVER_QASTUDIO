package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;


public interface TeamMemberCommandService {
    void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember, Long userId);
    TeamMemberResponse.AcceptInvitation inviteMemberWithToken(String token, Long userId);
    TeamMemberResponse.AcceptInvitation inviteMemberWithEmailAndToken(String email, String token, Long userId);

    void changePermission(TeamMemberRequest.ChangePermission changePermission, Long projectId, Long userId);
}
