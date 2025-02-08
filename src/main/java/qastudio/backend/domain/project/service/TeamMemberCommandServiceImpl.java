package qastudio.backend.domain.project.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import qastudio.backend.domain.auth.converter.EmailConverter;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.auth.service.AuthQueryService;
import qastudio.backend.domain.auth.service.EmailQueryService;
import qastudio.backend.domain.project.converter.TeamMemberConverter;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.exception.custom.TeamMemberException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.InviteTokenProvider;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMemberCommandServiceImpl implements TeamMemberCommandService{

    private final AccountTableRepository accountTableRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final InviteTokenProvider inviteTokenProvider;
    private final EmailQueryService emailQueryService;

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

    @Override
    public void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember) {

        // 삭제하고자 하는 유저
        Long userId = deleteMember.getUserId();
        String email = deleteMember.getEmail();

        // user의 이메일 정보가 요청을 보낸 이메일과 맞는지 확인
        boolean match = accountTableRepository.existsByUserIdAndEmail(userId, email);
        if (!match) {
            throw new BadRequestException(ErrorStatus.UNMATCHED_USER);
        }

        // UserProject 조회
        List<UserProject> userProjects = userProjectRepository.findByProjectId(projectId);

        // 유저 삭제
        userProjects.stream()
                .filter(userProject -> userProject.getUser().getId().equals(userId)) // userId가 일치하는 항목 필터링
                .forEach(userProjectRepository::delete);

    }

    @Override
    public Long inviteMember(String token) {
        Claims claims = inviteTokenProvider.validateToken(token);

        Long projectId = claims.get("projectId", Long.class);
        Long userId = claims.get("userId", Long.class);
        String email = claims.get("email", String.class);

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        // 중복 초대되었다면 생성하지 않고 projectId 응답
        boolean isAlreadyInvited = userProjectRepository.existsByUserIdAndProjectId(userId, projectId);
        if (isAlreadyInvited) {
            return projectId;
        }

        UserProject userProject = TeamMemberConverter.toUserProject(user, project, Role.MEMBER, email);
        userProjectRepository.save(userProject);

        return projectId;
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