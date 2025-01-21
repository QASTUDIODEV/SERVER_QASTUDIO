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
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.PageScenario;
import qastudio.backend.domain.project.dto.response.ProjectResponse.ProjectCreation;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageSceenario.PageScenarioRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;

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
    private final TeamMemberCommandService teamMemberCommandService;
    private final ProjectConverter projectConverter;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    @Value("${ai.base-url}")
    String baseUrl;
    @Value("${ai.project-information}")
    String projectInformationUrl;

    @Override
    public Project uploadProjectFile(Long userId, Long projectId, MultipartFile zipFile, String token) throws JsonProcessingException {

        // ai 서버에 프로젝트 정보 요청
        String response = getResponse(userId, projectId, zipFile, token);

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

    public ProjectCreation createProject(Long userId, ProjectRequest.CreateProject createProject) {
        // 프로젝트 저장
        Project newProject = projectConverter.toProject(createProject);
        Project savedProject = projectRepository.save(newProject);

        // 유저 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        // 프로젝트 생성자 (Leader) 설정
        UserProject userProject = UserProject.builder().user(user).project(savedProject).role(Role.LEADER).userEmail(null).build();
        userProjectRepository.save(userProject);

        // 팀원 초대
        List<TeamMemberRequest.MemberEmail> memberEmailList = createProject.getMemberEmailList();
        teamMemberCommandService.inviteMembers(newProject.getId(), memberEmailList);

        return projectConverter.toProjectCreationResponse(userProject, savedProject);
    }

    @Override
    public Project updateProjectIntroduction(Long projectId, ProjectRequest.UpdateIntroduce updateIntroduce) {
        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // introduction 수정
        project.updateIntroduction(updateIntroduce.getIntroduce());

        projectRepository.save(project);
        return project;
    }
}
