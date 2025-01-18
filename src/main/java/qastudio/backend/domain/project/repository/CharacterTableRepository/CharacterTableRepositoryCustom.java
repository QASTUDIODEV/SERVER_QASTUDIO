package qastudio.backend.domain.project.repository.CharacterTableRepository;

import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

public interface CharacterTableRepositoryCustom {
    List<CharacterTable> findAllByProjectId(Long projectId);

    List<Scenario> findAllByCharacterTableId(Long characterTableId);

    List<CharacterTable> findAllById(List<Long> ids);
}
