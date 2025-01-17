package qastudio.backend.domain.user.repository.User;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom{

}