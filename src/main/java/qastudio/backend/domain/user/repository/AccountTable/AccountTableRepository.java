package qastudio.backend.domain.user.repository.AccountTable;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.enums.EmailType;

import java.util.List;
import java.util.Optional;

public interface AccountTableRepository extends JpaRepository<AccountTable, Long>, AccountTableRepositoryCustom {
    Optional<AccountTable> findByEmailAndEmailType(String email, EmailType emailType);}
