package qastudio.backend.domain.project.repository.CharacterTableRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

public interface CharacterTableRepositoryCustom {
    List<CharacterTable> findAllByProjectId(Long projectId);
    List<CharacterTable> findAllById(List<Long> ids);
    Page<CharacterTable> findAllByProjectIdWithPage(Long projectId, Pageable pageable);
}
