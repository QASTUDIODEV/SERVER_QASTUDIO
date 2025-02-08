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
    public List<AccountTable> searchMember(Long projectId, String email) {
        // UserProject 조회
        List<UserProject> userProjects = userProjectRepository.findByProjectId(projectId);

        // 이미 가입된 유저들의 id 리스트
        List<Long> existingMemberIds = userProjects.stream()
                .map(userProject -> userProject.getUser().getId())
                .toList();

        // email에 해당하는 account 조회
        List<AccountTable> matchingAccounts = accountTableRepository.findByEmail(email);

        // 중복된 (userId, email) 조합 제거
        Set<String> uniqueUserEmailPairs = matchingAccounts.stream()
                .map(accountTable -> accountTable.getUser().getId() + ":" + accountTable.getEmail())
                .collect(Collectors.toSet());

        // UserProject에 해당하지 않는 User의 account 리스트 리턴
        return matchingAccounts.stream()
                .filter(accountTable -> uniqueUserEmailPairs.remove(accountTable.getUser().getId() + ":" + accountTable.getEmail()))  // 중복 제거
                .filter(accountTable -> !existingMemberIds.contains(accountTable.getUser().getId()))  // 기존 회원 제외
                .toList();
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

        memberEmailList
                .forEach(memberEmail -> {
                    Long userId = memberEmail.getUserId();
                    String email = memberEmail.getEmail();

                    // userId로 user 조회
                    User user = userRepository.findByUserId(userId)
                            .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

                    // user의 이메일 정보가 요청을 보낸 이메일과 맞는지 확인
                    boolean match = accountTableRepository.existsByUserIdAndEmail(userId, email);
                    if (!match) {
                        throw new BadRequestException(ErrorStatus.UNMATCHED_USER);
                    }

                    // 중복 초대 체크
                    boolean isAlreadyInvited = userProjectRepository.existsByUserIdAndProjectId(user.getId(), projectId);
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
