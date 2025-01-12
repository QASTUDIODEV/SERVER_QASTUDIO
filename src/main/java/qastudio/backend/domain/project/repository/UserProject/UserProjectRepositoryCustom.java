package qastudio.backend.domain.project.repository.UserProject;

import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface UserProjectRepositoryCustom {
    boolean existsByUserAndProject(Long userId, Long projectId);
    List<UserProject> findUserProjectsByProjectId(Long projectId);
    // 추후 유저가 초대된 프로젝트 리스트 조회에 사용
    List<UserProject> findUserProjectsByUserId(Long userId);
}
