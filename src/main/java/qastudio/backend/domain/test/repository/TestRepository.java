package qastudio.backend.domain.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.test.entity.Test;

public interface TestRepository  extends JpaRepository<Test, Long>, TestRepositoryCustom{
}
