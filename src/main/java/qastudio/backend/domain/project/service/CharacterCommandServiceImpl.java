package qastudio.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
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
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final FeatureRepository featureRepository;
    private final CharacterConverter characterConverter;
    private static final Logger logger = LoggerFactory.getLogger(CharacterCommandServiceImpl.class);

    @Value("${ai.base-url}")
    String baseUrl;
    String createScenarioUrl = "/api/v1/ai/project/character/create";


    @Override
    public CharacterScenario createCharacter(Long projectId, CreateCharacter createCharacter, String token) throws JsonProcessingException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        CharacterTable characterTable = characterConverter.toCharacter(createCharacter, project);
        CharacterTable savedCharacterTable = characterTableRepository.save(characterTable);

        for (String accessPagePath : createCharacter.getAccessPage()) {
            List<Page> pages = pageRepository.findAllByPaths(accessPagePath, projectId);

            if (pages.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("No pages found with path '%s' in project with ID '%d'.", accessPagePath, projectId));
            }

            for (Page page : pages) {
                PageRole pageRole = PageRole.builder()
                        .characterTable(savedCharacterTable)
                        .page(page)
                        .build();
                pageRoleRepository.save(pageRole);
            }
        }

        String assistantId = project.getAssistantId();
        String name = characterTable.getCharacterName();
        String description = characterTable.getCharacterDescription();

        String aiResponse = getResponse(assistantId, name, description, token);

        logger.info(aiResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(aiResponse);
        String dataJson = rootNode.path("data").asText();
        JsonNode dataNode = objectMapper.readTree(dataJson);

        Scenario scenario;
        try {
            String scenarioName = dataNode.path("title").asText();
            String scenarioDescription = dataNode.path("description").asText();
            String startPage = dataNode.path("start_path").asText();

            if (startPage == null || startPage.isEmpty()) {
                throw new IllegalArgumentException("Start path is missing or invalid in the AI response.");
            }

            logger.info("Extracted start_path: {}", startPage);
            logger.info("Looking for page with path '{}' and projectId '{}'", startPage, projectId);

            Page page = pageRepository.findByPath(startPage, projectId).orElseThrow(() -> {
                logger.info("Page not found with path: {} and projectId: {}", startPage, projectId);
                return new BadRequestException(ErrorStatus.PAGE_NOT_FOUND);});

            scenario = Scenario.builder()
                    .characterTable(characterTable)
                    .scenarioName(scenarioName)
                    .scenarioDescription(scenarioDescription)
                    .page(page)
                    .build();
            scenarioRepository.save(scenario);

            JsonNode scenariosArray = dataNode.path("scenarios");
            saveActionsAndFeatures(scenario, scenariosArray);

        } catch (Exception e) {
            logger.error("Error processing AI scenario JSON response: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while processing the AI scenario JSON response.");
        }

        List<ActionTable> actionTables = actionTableRepository.findByScenarioId(scenario.getId());

        return characterConverter.toCharacterScenario(
                savedCharacterTable,
                createCharacter.getAccessPage(),
                scenario,
                actionTables);
    }

    private String getResponse(String assistantId, String name, String description, String token) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(120))  // 타임아웃을 30초로 설정
                ))
                .build();

        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("assistant_id", assistantId);
            requestBody.put("name", name);
            requestBody.put("description", description);

            // 비동기 처리 추가 작업 필요
            return webClient.post()
                    .uri(createScenarioUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e){
            throw new RuntimeException("An error occurred while processing the AI scenario request.", e);
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

    @Override
    public CharacterScenario updateCharacter(Long characterId, UpdateCharacter updateCharacter) {
        // accessPage 수정 안 되는 문제 해결 필요

        CharacterTable characterTable = characterTableRepository.findById(characterId)
                .orElseThrow(() -> new EntityNotFoundException("역할이 존재하지 않습니다."));

        characterTable.updateCharacter(
                updateCharacter.getCharacterName(),
                updateCharacter.getCharacterDescription()
        );

        List<String> accessPagePaths = updateCharacter.getAccessPage();
        List<Page> requestedPages = pageRepository.findAllByPathIn(accessPagePaths);

        List<PageRole> existingPageRoles = pageRoleRepository.findAllByCharacterId(characterId);
        List<Page> existingPages = existingPageRoles.stream()
                .map(PageRole::getPage)
                .toList();

        List<Page> pagesToAdd = requestedPages.stream()
                .filter(page -> !existingPages.contains(page))
                .toList();

        for (Page page : pagesToAdd) {
            PageRole newPageRole = PageRole.builder()
                    .characterTable(characterTable)
                    .page(page)
                    .build();
            pageRoleRepository.save(newPageRole);
        }

        List<PageRole> rolesToRemove = existingPageRoles.stream()
                .filter(pageRole -> !requestedPages.contains(pageRole.getPage()))
                .collect(Collectors.toList());

        if (!rolesToRemove.isEmpty()) {
            pageRoleRepository.deleteAll(rolesToRemove);
        }

        characterTableRepository.save(characterTable);

        List<PageRole> updatedPageRoles = pageRoleRepository.findAllByCharacterId(characterId);
        return characterConverter.toCharacterScenarioResponse(characterTable, updatedPageRoles);
    }

    public void deleteCharacters(List<Long> characterIds) {
        List<CharacterTable> charactersToDelete = characterTableRepository.findAllById(characterIds);

        if (charactersToDelete.size() != characterIds.size()) {
            throw new EntityNotFoundException("일부 역할이 존재하지 않습니다.");
        }

        characterTableRepository.deleteAll(charactersToDelete);
    }
}
