package qastudio.backend.domain.user.repository.AccountTable;

import qastudio.backend.domain.user.entity.AccountTable;

public interface AccountTableRepositoryCustom {
    boolean existsByUserIdAndEmail(Long userId, String email);
}
