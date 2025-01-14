package qastudio.backend.domain.user.repository.AccountTable;

import qastudio.backend.domain.user.entity.AccountTable;

import java.util.List;

public interface AccountTableRepositoryCustom {
    boolean existsByUserIdAndEmail(Long userId, String email);
    List<AccountTable> findAccountsByEmail(String email);
}
