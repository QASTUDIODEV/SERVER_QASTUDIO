package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.user.entity.AccountTable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberQueryServiceImpl implements TeamMemberQueryService {
    @Override
    public List<TeamMemberResponse.Member> inviteMembers(Long projectId, TeamMemberRequest.@Valid EmailList inviteMembers) {
        return List.of();
    }

    @Override
    public List<TeamMemberResponse.Member> getTeamMemberList(Long projectId) {
        return List.of();
    }

    @Override
    public List<TeamMemberResponse.Member> deleteMembers(Long projectId, TeamMemberRequest.EmailList deleteMembers) {
        return List.of();
    }

    @Override
    public List<AccountTable> searchMember(Long projectId, String email) {
        return List.of();
    }
}
