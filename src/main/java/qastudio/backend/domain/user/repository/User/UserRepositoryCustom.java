package qastudio.backend.domain.user.repository.User;

import qastudio.backend.domain.user.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Integer countProjectsByUserId(Long userId);
}
