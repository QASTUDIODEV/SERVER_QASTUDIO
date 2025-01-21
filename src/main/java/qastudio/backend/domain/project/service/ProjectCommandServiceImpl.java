package qastudio.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.PageScenario;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageSceenario.PageScenarioRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final PageRepository pageRepository;
    private final ProjectRepository projectRepository;
    private final PageScenarioRepository pageScenarioRepository;

    @Value("${ai.base-url}")
    String baseUrl;
    @Value("${ai.project-information}")
    String projectInformationUrl;

    @Override
    public Project uploadProjectFile(Long userId, Long projectId, MultipartFile zipFile, String token) throws JsonProcessingException {

        // ai 서버에 프로젝트 정보 요청
        String response = getResponse(userId, projectId, zipFile, token);
        // 응답 값 확인
        System.out.println(response);

        // 프로젝트 정보 수정
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(response);

            // assistant_id
            String assistantId = rootNode.path("data").path("assistant_id").asText();

            // project
            JsonNode projectNode = rootNode.path("data").path("response");
            JsonNode projectData = objectMapper.readTree(projectNode.asText()).path("project");
            String projectDescription = projectData.path("description").asText();
            String projectViewType = projectData.path("view_type").asText();
            String projectDevelopmentSkill = projectData.path("development_skill").asText();

            System.out.println(projectDevelopmentSkill);
            System.out.println(projectData);
            System.out.println(projectViewType);

            // !! - projectDevelopmentSkill 처리해야 함
            project.updateProjectInfo(assistantId, projectDescription, projectViewType, projectDevelopmentSkill);
            projectRepository.save(project);

            // page
            JsonNode pagesNode = objectMapper.readTree(projectNode.asText()).path("pages");

            for (JsonNode jsonPage : pagesNode) {
                String pageName = jsonPage.path("name").asText();
                String pagePath = jsonPage.path("path").asText();
                String pageDescription = jsonPage.path("description").asText();

                // Data too long for column 'page_description : 임시 수정한 것, 수정 필요
                if (pageDescription.length() > 100) {
                    pageDescription = pageDescription.substring(0, 100);
                }

                Page page = Page.builder()
                        .project(project)
                        .pageName(pageName)
                        .path(pagePath)
                        .pageDescription(pageDescription)
                        .build();

                Page savedPage = pageRepository.save(page);

                // scenario
                JsonNode scenarioNode = jsonPage.path("senario").path("steps");
                if (scenarioNode.isArray()) {
                    List<PageScenario> scenarios = new ArrayList<>();

                    for (JsonNode step : scenarioNode) {
                        String stepContent = step.asText();

                        PageScenario pageScenario = PageScenario.builder()
                                .content(stepContent)
                                .page(savedPage)
                                .build();

                        scenarios.add(pageScenario);
                    }

                    pageScenarioRepository.saveAll(scenarios);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("JSON 응답 처리 중 오류 발생", e);
        }

        return project;
    }

    private String getResponse(Long userId, Long projectId, MultipartFile zipFile, String token) {
        // WebClient 객체 생성
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        // zip 파일을 AI 서버로 전송
        try {
            File tempFile = File.createTempFile("upload", ".zip");
            zipFile.transferTo(tempFile);

            return webClient.post()
                    .uri(projectInformationUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("user_id", userId.toString())
                            .with("project_id", projectId.toString())
                            .with("file", new FileSystemResource(tempFile)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류 발생", e);
        }
    }
}
