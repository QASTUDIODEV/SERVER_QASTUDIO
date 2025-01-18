package qastudio.backend.domain.user.repository.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.entity.User;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Integer countProjectsByUserId(Long userId);
    Page<UserProject> findAllByUser(User user, PageRequest pageRequest);
}
