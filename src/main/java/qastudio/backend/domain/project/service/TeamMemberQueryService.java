package qastudio.backend.domain.project.service;

import jakarta.validation.Valid;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;

import java.util.List;

public interface TeamMemberQueryService {
    List<UserProject> getTeamMemberList(Long projectId);

    List<AccountTable> searchMember(Long projectId, String email);
}
