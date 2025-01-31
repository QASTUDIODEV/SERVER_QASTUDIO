package qastudio.backend.domain.project.repository.CharacterTableRepository;

import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

public interface CharacterTableRepositoryCustom {
    List<CharacterTable> findAllByProjectId(Long projectId);

    List<CharacterTable> findAllById(List<Long> ids);
}
