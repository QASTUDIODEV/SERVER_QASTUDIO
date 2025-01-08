package qastudio.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.user.entity.AccountTable;

import java.util.Optional;

public interface AccountTableRepository extends JpaRepository<AccountTable, Long> {
    Boolean existsByEmail(String email);
    Optional<AccountTable> findByEmail(String email);
}
