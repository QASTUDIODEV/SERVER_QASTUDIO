package qastudio.backend.domain.project.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface TeamMemberCommandService {
    void inviteMembers(Long projectId, List<TeamMemberRequest.MemberEmail> memberEmailList);
    void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember);
    Long inviteMember(String token);
}
