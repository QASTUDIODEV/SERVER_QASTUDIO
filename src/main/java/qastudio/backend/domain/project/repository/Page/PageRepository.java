package qastudio.backend.domain.project.repository.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.Page;


public interface PageRepository extends JpaRepository<Page, Long>, PageRepositoryCustom {

}
