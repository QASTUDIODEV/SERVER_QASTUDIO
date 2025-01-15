package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.exception.custom.TeamMemberException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberCommandServiceImpl implements TeamMemberCommandService{

    private final AccountTableRepository accountTableRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<TeamMemberResponse.Member> inviteMembers(TeamMemberRequest.Invite inviteMembers) {

        Long projectId = inviteMembers.getProjectId();

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        return inviteMembers.getMemberEmailList().stream()
                .map(memberEmail -> {

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

                    // 중복 초대 체크 (기준 확인 필요)
                    boolean isAlreadyInvited = userProjectRepository.existsByUserIdAndProjectId(user.getId(), projectId);
                    if (isAlreadyInvited) {
                        throw new TeamMemberException(ErrorStatus.ALREADY_REGISTERED_MEMBER);
                    }

                    // 팀원 초대
                    UserProject userProject = UserProject.builder()
                            .user(user)
                            .project(project)
                            .role(Role.MEMBER)
                            .userEmail(email)
                            .build();
                    userProjectRepository.save(userProject);

                    // dto 응답 추가
                    return TeamMemberResponse.Member.builder()
                            .userId(userId)
                            .projectRole(userProject.getRole())
                            .email(email)
                            .nickname(user.getNickname())
                            .profileImage(user.getProfileImage())
                            .build();
                })
                .collect(Collectors.toList());

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
}
