package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.user.entity.User;

public class UserProjectConverter {
    public static UserProject toMemberUserProject(User user, Project project, String email) {
        return UserProject.builder()
                .user(user)
                .project(project)
                .role(Role.MEMBER)
                .userEmail(email)
                .build();
    }
}
