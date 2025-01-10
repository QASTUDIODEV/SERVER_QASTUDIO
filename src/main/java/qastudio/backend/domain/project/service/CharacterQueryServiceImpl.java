package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterQueryServiceImpl implements CharacterQueryService{
    @Override
    public List<CharacterTable> getProjectCharacter(Long projectId) {
        return List.of();
    }
}
