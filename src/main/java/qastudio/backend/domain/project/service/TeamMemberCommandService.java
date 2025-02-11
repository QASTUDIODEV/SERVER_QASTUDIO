package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;


public interface TeamMemberCommandService {
    void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember);
    TeamMemberResponse.AcceptInvitation inviteMemberWithToken(String token, Long userId);
    TeamMemberResponse.AcceptInvitation inviteMemberWithEmailAndToken(String email, String token, Long userId);
}
