package qastudio.backend.domain.project.repository.CharacterTableRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.CharacterTable;

public interface CharacterTableRepository extends JpaRepository<CharacterTable, Long>, CharacterTableRepositoryCustom {
}
