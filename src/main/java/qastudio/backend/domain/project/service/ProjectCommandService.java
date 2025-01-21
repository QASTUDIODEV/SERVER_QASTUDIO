package qastudio.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.entity.Project;

public interface ProjectCommandService {
    Project uploadProjectFile(Long userId, Long projectId, MultipartFile zipFile, String token) throws JsonProcessingException;

    Project updateProjectIntroduction(Long projectId, ProjectRequest.UpdateIntroduce updateIntroduce);

}
