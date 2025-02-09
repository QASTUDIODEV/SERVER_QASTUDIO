package qastudio.backend.domain.project.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.service.EmailQueryService;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.exception.custom.TeamMemberException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.InviteTokenProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TeamMemberQueryServiceImpl implements TeamMemberQueryService {

    private final AccountTableRepository accountTableRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final InviteTokenProvider inviteTokenProvider;
    private final EmailQueryService emailQueryService;

    @Override
    public List<UserProject> getTeamMemberList(Long projectId) {
        // UserProject 조회
        return userProjectRepository.findByProjectId(projectId);

    }


    @Override
    public boolean searchMember(Long projectId, String email) {
        // 1. 회원가입 여부 확인 (AccountTable에서 해당 이메일 존재 여부)
        boolean isRegistered = accountTableRepository.existsByEmail(email);

        if (!isRegistered) {
            return false;  // 회원가입되지 않은 경우 false 반환
        }

        // 2. 이미 프로젝트에 가입된 유저 확인
        List<UserProject> userProjects = userProjectRepository.findByProjectId(projectId);

        // 프로젝트에 가입된 유저들의 이메일 리스트 생성
        Set<String> existingMemberEmails = userProjects.stream()
                .flatMap(userProject -> userProject.getUser().getAccounts().stream())
                .map(AccountTable::getEmail)
                .collect(Collectors.toSet());

        // 3. 만약 이미 프로젝트에 가입된 유저라면 예외 발생
        if (existingMemberEmails.contains(email)) {
            throw new TeamMemberException(ErrorStatus.ALREADY_REGISTERED_MEMBER);
        }

        // 4. 회원이면서 아직 프로젝트에 가입되지 않은 경우
        return true;
    }

    @Override
    public List<UserProject> getTeamMemberExceptLeader(Long projectId) {
        // UserProject 조회
        return userProjectRepository.findByProjectIdExcludingLeader(projectId);    }

    @Override
    public void inviteMembers(Long projectId, List<TeamMemberRequest.MemberEmail> memberEmailList) {

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        memberEmailList.forEach(memberEmail -> {
            String email = memberEmail.getEmail();

            // AccountTable에서 email을 기준으로 userId 조회
            Long userId = accountTableRepository.findByEmail(email).stream()
                    .map(AccountTable::getUser)
                    .map(User::getId)
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

            // 중복 초대 체크
            boolean isAlreadyInvited = userProjectRepository.existsByUserIdAndProjectId(userId, projectId);
            if (isAlreadyInvited) {
                throw new TeamMemberException(ErrorStatus.ALREADY_REGISTERED_MEMBER);
            }

            // 팀원 초대
            inviteMemberWithEmail(email, project.getProjectName(), formattedExpirationDate(), generateInvitationLink(projectId, userId, email));
        });
    }


    private void inviteMemberWithEmail(String email, String projectName, String expirationDate, String invitationLink) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("teamProjectName", projectName);
        variables.put("invitationLink", invitationLink);
        variables.put("expirationDate", expirationDate);

        try {
            emailQueryService.sendInviteEmail(email, variables);
            log.info("Invitation email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            log.info("Failed to send invitation email.");
        }
    }

    private String generateInvitationLink(Long projectId, Long userId, String email) {
        // JWT 토큰 생성
        String token = inviteTokenProvider.generateToken(projectId, userId, email);

        // 초대 링크 생성
        return "https://www.qa-studio.com/invite?=" + token;
    }

    private String formattedExpirationDate() {
        // 현재 날짜로부터 7일 후
        LocalDate expirationDate = LocalDate.now().plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return expirationDate.format(formatter);
    }
}
