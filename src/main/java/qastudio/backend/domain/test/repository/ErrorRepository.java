package qastudio.backend.domain.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.test.entity.Error;

public interface ErrorRepository extends JpaRepository<Error, Long>, ErrorRepositoryCustom {
}
