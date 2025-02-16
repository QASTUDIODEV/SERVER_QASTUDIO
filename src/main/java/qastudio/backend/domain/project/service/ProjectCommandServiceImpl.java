package qastudio.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.converter.UserProjectConverter;
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
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.exception.custom.TeamMemberException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageSceenario.PageScenarioRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final PageRepository pageRepository;
    private final ProjectRepository projectRepository;
    private final PageScenarioRepository pageScenarioRepository;
    private final TeamMemberCommandService teamMemberCommandService;
    private final ProjectConverter projectConverter;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final TeamMemberQueryService teamMemberQueryService;
    private final AccountTableRepository accountTableRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${ai.base-url}")
    String baseUrl;
    @Value("${ai.project-information}")
    String projectInformationUrl;

    @Override
    public Project uploadProjectFile(Long userId, Long projectId, MultipartFile zipFile, String token) throws JsonProcessingException {
        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // ai 서버에 프로젝트 정보 요청
        String response = getResponse(userId, projectId, zipFile, token);
        log.info(response);

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
        UserProject userProject = UserProject.builder().user(user).project(savedProject).role(Role.LEADER).userEmail(user.getAccounts().get(0).getEmail()).build();
        userProjectRepository.save(userProject);

        // 팀원 초대
        List<TeamMemberRequest.MemberEmail> memberEmailList = createProject.getMemberEmailList();
        teamMemberQueryService.inviteMembers(newProject.getId(), memberEmailList);

        return projectConverter.toProjectCreationResponse(userProject, memberEmailList, savedProject);
    }

    @Override
    public void deleteProject(Long projectId, Long userId) {
        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        // 해당 유저가 프로젝트 팀 멤버인지 확인
        boolean isMember = project.getUserProjects().stream()
                .filter(userProject -> userProject != null && userProject.getUser() != null)
                .anyMatch(userProject -> userProject.getUser().getId().equals(userId));

        if (!isMember) {
            throw new BadRequestException(ErrorStatus.USER_NOT_TEAM_MEMBER);
        }

        // 해당 유저가 프로젝트의 LEADER인지 확인
        boolean isLeader = project.getUserProjects().stream()
                .filter(userProject -> userProject != null && userProject.getUser() != null)
                .anyMatch(userProject -> userProject.getUser().getId().equals(userId)
                        && userProject.getRole() == Role.LEADER);

        if (!isLeader) {
            throw new BadRequestException(ErrorStatus.USER_NOT_LEADER);
        }

        projectRepository.delete(project);
    }

    @Override
    public void updateProject(Long userId, Long projectId, ProjectRequest.UpdateProject updateProject) {
        // 현재 요청을 보낸 사용자가 해당 프로젝트의 LEADER인지 확인
        UserProject requestingUserProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_TEAM_MEMBER));

        if (!requestingUserProject.getRole().equals(Role.LEADER)) {
            throw new TeamMemberException(ErrorStatus.USER_NOT_LEADER);
        }

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 프로젝트 정보 수정
        project.updateProject(updateProject.getProjectName(), updateProject.getProjectImage(), updateProject.getProjectUrl());

        // UserProject 조회 (LEADER 제외)
        List<UserProject> userProjects = userProjectRepository.findByProjectIdExcludingLeader(projectId);

        List<String> memberEmails = userProjects.stream()
                .map(UserProject::getUserEmail)
                .toList();

        // 초대를 수락하지 않은 유저 조회
        List<String> unacceptedMembers = teamMemberQueryService.getInvitationEmails(projectId);

        // 프로젝트 초대에 승낙 & 승낙하지 않은 유저 모두 조회
        List<String> existingUserEmails = Stream.concat(memberEmails.stream(), unacceptedMembers.stream())
                .distinct()
                .toList();

        // 새로운 요청된 이메일 목록
        Set<String> newUserEmails = updateProject.getMemberEmailList().stream()
                .map(TeamMemberRequest.MemberEmail::getEmail)
                .collect(Collectors.toSet());

        // 삭제할 유저 이메일 (새로 요청된 이메일 목록에 포함되지 않은 유저)
        List<String> usersToRemove = existingUserEmails.stream()
                .filter(up -> !newUserEmails.contains(up))
                .toList();

        // 추가할 유저 초대 이메일 보내기
        for (String email : newUserEmails) {
            if (!existingUserEmails.contains(email)) {
                // AccountTable에서 email을 기준으로 userId 조회
                Long newUserId = accountTableRepository.findByEmail(email).stream()
                        .map(AccountTable::getUser)
                        .map(User::getId)
                        .findFirst()
                        .orElse(-1L);

                // 초대 메일 전송
                teamMemberQueryService.inviteMember(email, project, newUserId);
                // redis 저장
                teamMemberQueryService.saveInvitationEmail(projectId, email, 604800000);
            }
        }

        // userProjects에서 필터링하여 삭제할 UserProject 객체 필터링
        List<UserProject> userProjectsToRemove = userProjects.stream()
                .filter(up -> usersToRemove.contains(up.getUserEmail()))
                .toList();

        // 기존 팀원 삭제
        userProjectRepository.deleteAll(userProjectsToRemove);

        // unacceptedEmailList 중 newUserEmails에 없는 이메일을 제거 (승낙하지 못하도록)
        for (String email : unacceptedMembers) {
            if (!newUserEmails.contains(email)) {
                removeInvitationEmail(projectId, email);
            }
        }

    }

    private void removeInvitationEmail(Long projectId, String email) {
        String redisKey = "invite:" + projectId + ":" + email;
        redisTemplate.delete(redisKey);
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
