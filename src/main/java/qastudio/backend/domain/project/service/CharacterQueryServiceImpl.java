package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterQueryServiceImpl implements CharacterQueryService{

    private final CharacterTableRepository characterRepository;
    private final CharacterConverter characterConverter;
    private final PageRepository pageRepository;
    private final ProjectRepository projectRepository;

    @Override
    public CharacterResponse.DetailCharacterList getDetailCharacterList(Long projectId) {
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        List<CharacterTable> characterTables = characterRepository.findAllByProjectId(projectId); // N+1 문제 해결 필요
        return characterConverter.toDetailCharacterList(characterTables);
    }

    @Override
    public CharacterResponse.ProjectPathList getProjectPaths(Long projectId) {
        List<Page> pages = pageRepository.findAllByProjectId(projectId);
        return CharacterConverter.toProjectPathList(pages);
    }
}
