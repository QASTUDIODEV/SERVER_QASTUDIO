package qastudio.backend.domain.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.CharacterTable;

public interface CharacterTableRepository extends JpaRepository<CharacterTable, Long> {
}
