package qastudio.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.request.CharacterRequest.CreateCharacter;
import qastudio.backend.domain.project.dto.request.CharacterRequest.UpdateCharacter;
import qastudio.backend.domain.project.dto.response.CharacterResponse.CharacterScenario;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.PageRole;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageRole.PageRoleRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.FeatureRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AiServerException;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import org.springframework.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterCommandServiceImpl implements CharacterCommandService {

    private final CharacterTableRepository characterTableRepository;
    private final ProjectRepository projectRepository;
    private final ScenarioRepository scenarioRepository;
    private final PageRoleRepository pageRoleRepository;
    private final PageRepository pageRepository;
    private final ActionTableRepository actionTableRepository;
    private final UserRepository userRepository;
    private final FeatureRepository featureRepository;
    private final CharacterConverter characterConverter;
    private static final Logger logger = LoggerFactory.getLogger(CharacterCommandServiceImpl.class);

    @Value("${ai.base-url}")
    String baseUrl;
    String createScenarioUrl = "/api/v1/ai/project/character/create";


    @Override
    public CharacterScenario createCharacter(Long userId, Long projectId, CreateCharacter createCharacter, String token) throws JsonProcessingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        CharacterTable characterTable = characterConverter.toCharacter(createCharacter, user, project);
        CharacterTable savedCharacterTable = characterTableRepository.save(characterTable);

        for (String accessPagePath : createCharacter.getAccessPage()) {
            List<Page> pages = pageRepository.findAllByPaths(accessPagePath, projectId);

            if (pages.isEmpty()) {
                throw new BadRequestException(ErrorStatus.PAGES_NOT_FOUND);
            }

            for (Page page : pages) {
                PageRole pageRole = PageRole.builder()
                        .characterTable(savedCharacterTable)
                        .page(page)
                        .build();
                pageRoleRepository.save(pageRole);
            }
        }

        Long scenarioId = null; // 시나리오 처음 생성할 때
        Scenario scenario = createScenario(user, project, characterTable, token, scenarioId);
        List<ActionTable> actionTables = actionTableRepository.findByScenarioId(scenario.getId());

        return characterConverter.toCharacterScenario(
                savedCharacterTable,
                createCharacter.getAccessPage(),
                scenario,
                actionTables);
    }

    @Override
    public CharacterScenario updateCharacter(Long userId, Long projectId, Long characterId, Long scenarioId, UpdateCharacter updateCharacter, String token) throws JsonProcessingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        CharacterTable existingCharacterTable = characterTableRepository.findById(characterId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.CHARACTER_NOT_FOUND));

        for (String accessPagePath : updateCharacter.getAccessPage()) {
            List<Page> pages = pageRepository.findAllByPaths(accessPagePath, projectId);
            logger.info("pages {}", pages);

            if (pages.isEmpty()) {
                throw new BadRequestException(ErrorStatus.PAGES_NOT_FOUND);
            }

            List<PageRole> existingPageRoles = pageRoleRepository.findAllByCharacterId(characterId);
            List<Page> existingPages = existingPageRoles.stream()
                    .map(PageRole::getPage)
                    .toList();
            logger.info("existingPages {}", existingPages);

            List<Page> pagesToAdd = pages.stream()
                    .filter(page -> !existingPages.contains(page))
                    .toList();
            logger.info("pagesToAdd {}", pagesToAdd);

            for (Page page : pagesToAdd) {
                PageRole newPageRole = PageRole.builder()
                        .characterTable(existingCharacterTable)
                        .page(page)
                        .build();
                pageRoleRepository.save(newPageRole);
            }

        }
        existingCharacterTable.update(updateCharacter);

        Scenario updatedScenario = createScenario(user, project, existingCharacterTable, token, scenarioId);
        List<ActionTable> actionTables = actionTableRepository.findByScenarioId(updatedScenario.getId());

        return characterConverter.toCharacterScenario(
                existingCharacterTable,
                updateCharacter.getAccessPage(),
                updatedScenario,
                actionTables);
    }

    public void deleteCharacters(List<Long> characterIds) {
        if (characterIds == null || characterIds.isEmpty()) {
            throw new BadRequestException(ErrorStatus.INVALID_CHARACTER_IDS);
        }

        // Null 값을 제거하여 안전한 ID 리스트 만들기
        List<Long> filteredCharacterIds = characterIds.stream()
                .filter(Objects::nonNull)
                .toList();
        if (filteredCharacterIds.isEmpty()) {
            throw new BadRequestException(ErrorStatus.INVALID_CHARACTER_IDS);
        }

        List<CharacterTable> charactersToDelete = characterTableRepository.findAllById(characterIds);

        if (charactersToDelete == null || charactersToDelete.isEmpty()) {
            charactersToDelete = Collections.emptyList();
        }

        if (charactersToDelete.size() != characterIds.size()) {
            throw new BadRequestException(ErrorStatus.CHARACTERS_NOT_FOUND);
        }

        characterTableRepository.deleteAll(charactersToDelete);
    }

    private String getAiResponse(String assistantId, String name, String description, List<String> pathList, String token) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(120))  // 타임아웃을 120초로 설정
                ))
                .build();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String pathListJson = objectMapper.writeValueAsString(pathList);

            Map<String, String> requestBody = new LinkedHashMap<>();
            requestBody.put("assistant_id", assistantId);
            requestBody.put("name", name);
            requestBody.put("description", description);
            requestBody.put("path_list", pathListJson);

            logger.info(requestBody.toString());
            String json = objectMapper.writeValueAsString(requestBody);
            logger.info(json);  // JSON 형식으로 출력


            // 비동기 처리 추가 작업 필요
            return webClient.post()
                    .uri(createScenarioUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new AiServerException("AI 서버 내부 오류: " + errorBody)))
                    )
                    .bodyToMono(String.class)
                    .block();
        } catch (AiServerException e) {
            logger.error(String.valueOf(e));
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 서버 오류 발생", e);
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 서버 통신 중 알 수 없는 오류 발생", e);
        }
    }

    private Scenario createScenario (User user, Project project, CharacterTable characterTable, String token, Long scenarioId) throws JsonProcessingException {

        String assistantId = project.getAssistantId();
        String name = characterTable.getCharacterName();
        String description = characterTable.getCharacterDescription();
        List<Page> pages = pageRepository.findAllByProjectId(project.getId());
        List<String> pathList = pages.stream()
                .map(Page::getPath)
                .toList();
        String aiResponse = getAiResponse(assistantId, name, description, pathList, token);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(aiResponse);
        String dataJson = rootNode.path("data").asText();
        JsonNode dataNode = objectMapper.readTree(dataJson);

        logger.info(aiResponse);

        Scenario scenario;
        try {
            String scenarioName = dataNode.path("title").asText();
            String scenarioDescription = dataNode.path("description").asText();
            String startPage = dataNode.path("start_path").asText();
            JsonNode scenariosArray = dataNode.path("scenarios");

            if (startPage == null || startPage.isEmpty()) {
                throw new AiServerException("Start page is missing or invalid in the AI response.");
            }

            Page page = pageRepository.findByPath(startPage, project.getId()).orElseThrow(() -> {
                logger.info("Page not found with path: {} and projectId: {}", startPage, project.getId());
                return new BadRequestException(ErrorStatus.START_PAGE_NOT_FOUND);});

            if (scenarioId == null) {
                scenario = Scenario.builder()
                        .characterTable(characterTable)
                        .scenarioName(scenarioName)
                        .scenarioDescription(scenarioDescription)
                        .page(page)
                        .user(user)
                        .build();
                scenarioRepository.save(scenario);
                saveActionsAndFeatures(scenario, scenariosArray);

                return scenario;
            } else {
                Scenario existingScenario = scenarioRepository.findById(scenarioId)
                        .orElseThrow(() -> new BadRequestException(ErrorStatus.SCENARIO_NOT_FOUND));
                existingScenario.update(scenarioName, scenarioDescription, characterTable, page);
                scenarioRepository.save(existingScenario);

                List<ActionTable> existingActions = actionTableRepository.findByScenarioId(scenarioId);
                actionTableRepository.deleteAll(existingActions);
                saveActionsAndFeatures(existingScenario, scenariosArray);

                return existingScenario;
            }

        } catch (BadRequestException | AiServerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing AI scenario JSON response: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while processing the AI scenario JSON response.");
        }
    }

    private void saveActionsAndFeatures(Scenario scenario, JsonNode scenariosArray) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        for (JsonNode scenarioNode : scenariosArray) {
            JsonNode element = scenarioNode.path("element");
            JsonNode locator = element.path("locator");
            JsonNode action = element.path("action");

            int step = element.path("step").asInt();
            String actionName = element.path("name").asText();
            String type = element.path("type").asText();

            ObjectNode featureJsonNode = objectMapper.createObjectNode();
            featureJsonNode.set("locator", locator);
            featureJsonNode.set("action", action);
            String featureJson = objectMapper.writeValueAsString(featureJsonNode);

            ActionTable actionTable = ActionTable.builder()
                    .step(step)
                    .actionDescription(actionName)
                    .actionType(type)
                    .scenario(scenario)
                    .build();
            actionTableRepository.save(actionTable);

            Feature feature = Feature.builder()
                    .featureJson(featureJson)
                    .action(actionTable)
                    .user(null)
                    .build();
            featureRepository.save(feature);
        }
    }
}
