package qastudio.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
