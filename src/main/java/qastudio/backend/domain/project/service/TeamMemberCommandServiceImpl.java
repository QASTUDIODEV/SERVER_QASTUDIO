package qastudio.backend.domain.project.service;

import ch.qos.logback.core.rolling.helper.TokenConverter;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.TeamMemberConverter;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
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

import java.util.*;

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
    private final StringRedisTemplate redisTemplate;


    @Override
    public void deleteMembers(Long projectId, TeamMemberRequest.MemberEmail deleteMember) {
        String email = deleteMember.getEmail();

        // 삭제하고자 하는 유저
        Long userId = accountTableRepository.findByEmail(email).stream()
                .map(AccountTable::getUser)
                .map(User::getId)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

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
    public TeamMemberResponse.AcceptInvitation inviteMemberWithToken(String token, Long userId) {
        if (token == null || token.trim().isEmpty()) {
            throw new TeamMemberException(ErrorStatus.TOKEN_MISSING);
        }

        Claims claims = inviteTokenProvider.validateToken(token);

        Long projectId = claims.get("projectId", Long.class);
        Long tokenUserId = claims.get("userId", Long.class);

        if (tokenUserId == -1) {
            return TeamMemberConverter.toAcceptInvitation(projectId);
        }

        if (!tokenUserId.equals(userId)) {
            throw new TeamMemberException(ErrorStatus.UNAUTHORIZED_INVITATION);
        }

        String email = claims.get("email", String.class);

        // 로그인한 유저와 projectId redis에 있는지 확인
        if (!isInvitationValid(projectId, email)) {
            throw new TeamMemberException(ErrorStatus.INVALID_INVITATION);
        }

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));


        // 중복 초대되었다면 생성하지 않고 projectId 응답
        boolean isAlreadyInvited = userProjectRepository.existsByUserIdAndProjectId(userId, projectId);
        if (isAlreadyInvited) {
            return TeamMemberConverter.toAcceptInvitation(projectId);
        }

        // 초대 이메일 삭제
        removeInvitationEmail(projectId, email);

        UserProject userProject = TeamMemberConverter.toUserProject(user, project, Role.MEMBER, email);
        userProjectRepository.save(userProject);

        return TeamMemberConverter.toAcceptInvitation(projectId);
    }

    @Override
    public TeamMemberResponse.AcceptInvitation inviteMemberWithEmailAndToken(String email, String token, Long userId) {
        if (token == null || token.trim().isEmpty()) {
            throw new TeamMemberException(ErrorStatus.TOKEN_MISSING);
        }

        Claims claims = inviteTokenProvider.validateToken(token);
        Long projectId = claims.get("projectId", Long.class);
        Long tokenUserId = claims.get("userId", Long.class);

        if (tokenUserId == -1) {
            // 초대하고자 하는 유저
            tokenUserId = accountTableRepository.findByEmail(email).stream()
                    .map(AccountTable::getUser)
                    .map(User::getId)
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
        }

        if (!userId.equals(tokenUserId)) {
            throw new TeamMemberException(ErrorStatus.UNAUTHORIZED_INVITATION);
        }

        // 중복 초대되었는지 확인
        boolean isAlreadyInvited = userProjectRepository.existsByUserIdAndProjectId(userId, projectId);
        if (isAlreadyInvited) {
            throw new TeamMemberException(ErrorStatus.ALREADY_REGISTERED_MEMBER);
        }

        // 로그인한 유저와 projectId redis에 있는지 확인
        if (!isInvitationValid(projectId, email)) {
            throw new TeamMemberException(ErrorStatus.INVALID_INVITATION);
        }

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        UserProject userProject = TeamMemberConverter.toUserProject(user, project, Role.MEMBER, email);
        userProjectRepository.save(userProject);

        // 초대 이메일 삭제
        removeInvitationEmail(projectId, email);

        return TeamMemberConverter.toAcceptInvitation(projectId);
    }

    private void removeInvitationEmail(Long projectId, String email) {
        String redisKey = "invite:" + projectId + ":" + email;
        redisTemplate.delete(redisKey);
    }

    // 프로젝트별 초대 이메일 검증
    private boolean isInvitationValid(Long projectId, String email) {
        String redisKey = "invite:" + projectId + ":" + email;
        return redisTemplate.opsForValue().get(redisKey) != null;
    }

}