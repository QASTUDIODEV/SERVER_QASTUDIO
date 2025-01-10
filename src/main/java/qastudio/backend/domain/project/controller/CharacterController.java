package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.dto.response.CharacterResponse.CharacterScenario;
import qastudio.backend.domain.project.dto.response.CharacterResponse.DetailCharacterList;
import qastudio.backend.domain.project.dto.response.CharacterResponse.ScenarioList;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.service.CharacterQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/character")
public class CharacterController {

    private final CharacterQueryService characterQueryService;

    @Operation(
            summary = "프로젝트 역할 조회 API",
            description = "프로젝트의 역할 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/{projectId}")
    public ApiResponse<CharacterResponse.ProjectCharacterList> getProjectCharacter (@PathVariable("projectId") Long projectId) {
        List<CharacterTable> characters = characterQueryService.getProjectCharacter(projectId);
        return ApiResponse.onSuccess(CharacterConverter.toProjectCharacterList(characters));
    }

    @Operation(
            summary = "프로젝트 별 역할 리스트 조회 API",
            description = "프로젝트 별로 역할 리스트를 조회합니다."
    )
    @GetMapping("/{projectId}/detail")
    public ApiResponse<CharacterResponse.DetailCharacterList> getCharacterDetailList (@PathVariable("projectId") Long projectId) {
        DetailCharacterList detailCharacters = characterQueryService.getDetailCharacterList(projectId);
        return ApiResponse.onSuccess(detailCharacters);
    }

    @Operation(
            summary = "역할 별 시나리오 리스트 조회 API",
            description = "역할 별로 시나리오 리스트를 조회합니다."
    )
    @GetMapping("/{characterId}/scenarios")
    public ApiResponse<CharacterResponse.ScenarioList> getScenarioLost (@PathVariable("characterId") Long characterId) {
        ScenarioList scenarioList = characterQueryService.getScenarioList(characterId);
        return ApiResponse.onSuccess(scenarioList);
    }

    @Operation(
            summary = "역할-시나리오 생성 API",
            description = "역할을 생성하며 ai에게 시나리오 생성을 요청합니다."
    )
    @PostMapping("")
    public ApiResponse<CharacterResponse.CharacterScenario> createCharacter (@RequestBody @Valid CharacterRequest.CreateCharacter createCharacter) {
        CharacterScenario characterScenario = characterQueryService.createCharacter(createCharacter);
        return ApiResponse.onSuccess(characterScenario);
    }

    @Operation(
            summary = "역할-시나리오 수정 API",
            description = "역할을 수정한 후, ai에게 시나리오 생성을 재요청합니다."
    )
    @PatchMapping("/{characterId}")
    public ApiResponse<CharacterResponse.CharacterScenario> updateCharacter (@PathVariable("characterId") Long characterId, @RequestBody @Valid CharacterRequest.UpdateCharacter updateCharacter) {
        CharacterScenario characterScenario = characterQueryService.updateCharacter(characterId, updateCharacter);
        return ApiResponse.onSuccess(characterScenario);
    }

}
