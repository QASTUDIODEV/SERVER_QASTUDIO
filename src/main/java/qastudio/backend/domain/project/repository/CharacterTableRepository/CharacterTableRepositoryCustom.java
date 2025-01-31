package qastudio.backend.domain.project.repository.CharacterTableRepository;

import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import java.util.Optional;

public interface CharacterTableRepositoryCustom {
    List<CharacterTable> findAllByProjectId(Long projectId);

    List<CharacterTable> findAllById(List<Long> ids);
}
