package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberQueryServiceImpl implements TeamMemberQueryService {

    private final AccountTableRepository accountTableRepository;
    private final UserProjectRepository userProjectRepository;

    @Override
    public List<TeamMemberResponse.Member> getTeamMemberList(Long projectId) {

        // UserProject 조회
        List<UserProject> userProjects = userProjectRepository.findByProjectId(projectId);

        // 유저 정보 반환
        return userProjects.stream()
                .map(userProject -> {
                    // User 조회
                    User user = userProject.getUser();

                    // dto 응답 추가
                    return TeamMemberResponse.Member.builder()
                            .userId(user.getId())
                            .userId(user.getId())
                            .projectRole(userProject.getRole())
                            .email(userProject.getUserEmail())
                            .nickname(user.getNickname())
                            .profileImage(user.getProfileImage())
                            .build();
                })
                .collect(Collectors.toList());

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

        // email에 해당하는 User의 id 리스트
        List<Long> matchingUserIds = matchingAccounts.stream()
                .map(accountTable -> accountTable.getUser().getId())
                .toList();

        // UserProject에 해당하지 않는 User의 account 리스트 리턴
        return matchingAccounts.stream()
                .filter(accountTable -> !existingMemberIds.contains(accountTable.getUser().getId()))
                .toList();
    }
}
