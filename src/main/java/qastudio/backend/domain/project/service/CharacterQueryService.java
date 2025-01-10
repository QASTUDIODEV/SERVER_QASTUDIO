package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

public interface CharacterQueryService {
    List<CharacterTable> getProjectCharacter(Long projectId);
}
