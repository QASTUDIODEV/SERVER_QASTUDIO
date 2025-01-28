package qastudio.backend.domain.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.dto.response.CharacterResponse.CharacterScenario;
import qastudio.backend.domain.project.dto.response.CharacterResponse.DetailCharacterList;
import qastudio.backend.domain.project.dto.response.CharacterResponse.ScenarioList;
import qastudio.backend.domain.project.service.CharacterCommandService;
import qastudio.backend.domain.project.service.CharacterQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.handler.annotation.Auth;
import qastudio.backend.global.security.jwt.JwtTokenFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/projects")
public class CharacterController {

    private final CharacterQueryService characterQueryService;
    private final CharacterCommandService characterCommandService;


    @Operation(
            summary = "프로젝트 별 역할 리스트 조회 API | by 챠리",
            description = "프로젝트 별로 역할 리스트를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.")
    })
    @GetMapping("/{projectId}/characters")
    public ApiResponse<CharacterResponse.DetailCharacterList> getCharacterDetailList (@PathVariable("projectId") Long projectId) {
        DetailCharacterList detailCharacterList = characterQueryService.getDetailCharacterList(projectId);
        return ApiResponse.onSuccess(detailCharacterList);
    }

    @Operation(
            summary = "역할 별 시나리오 리스트 조회 API | by 챠리",
            description = "역할 별로 시나리오 리스트를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER404", description = "The role does not exist."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.")
    })
    @GetMapping("/characters/{characterId}/scenarios")
    public ApiResponse<CharacterResponse.ScenarioList> getScenarioList (@PathVariable("characterId") Long characterId) {
        ScenarioList scenarioList = characterQueryService.getScenarioList(characterId);
        return ApiResponse.onSuccess(scenarioList);
    }

    @Operation(
            summary = "역할-시나리오 생성 API | by 챠리",
            description = "역할을 생성하며 ai에 시나리오 생성을 요청합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "역할-시나리오 생성 성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.")
    })
    @PostMapping("/{projectId}/characters")
    public ApiResponse<CharacterResponse.CharacterScenario> createCharacter (
            @Auth Long userId,
            @PathVariable("projectId") Long projectId,
            @RequestBody @Valid CharacterRequest.CreateCharacter createCharacter,
            @Parameter(hidden = true) HttpServletRequest request)
            throws JsonProcessingException {
        // 쿠키에서 JWT 토큰 추출
        String jwtToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }
        // 토큰이 없을 경우 예외 처리
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new AuthException(ErrorStatus.MISSING_AUTHORITY);
        }

        CharacterScenario characterScenario = characterCommandService.createCharacter(userId, projectId, createCharacter, jwtToken);
        return ApiResponse.onSuccess(characterScenario);
    }

    @Operation(
            summary = "역할-시나리오 수정 API | by 챠리",
            description = "역할을 수정한 후, ai에게 시나리오 생성을 재요청합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "역할-시나리오 수정 성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.")
    })
    @PatchMapping("/{projectId}/characters/{characterId}/scenarios/{scenarioId}")
    public ApiResponse<CharacterResponse.CharacterScenario> updateCharacter (
            @Auth Long userId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("characterId") Long characterId,
            @PathVariable("scenarioId") Long scenarioId,
            @RequestBody @Valid CharacterRequest.UpdateCharacter updateCharacter,
            @Parameter(hidden = true) HttpServletRequest request)
            throws JsonProcessingException {
        // 쿠키에서 JWT 토큰 추출
        String jwtToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }
        // 토큰이 없을 경우 예외 처리
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new AuthException(ErrorStatus.MISSING_AUTHORITY);
        }

        CharacterScenario characterScenario = characterCommandService.updateCharacter(userId, projectId, characterId, scenarioId, updateCharacter, jwtToken);
        return ApiResponse.onSuccess(characterScenario);
    }

    @Operation(
            summary = "역할 삭제 API | by 챠리",
            description = "역할을 삭제합니다. 역할에 종속된 시나리오도 함께 일괄 삭제됩니다. 단일 역할을 삭제할 수도 있고, 여러 역할을 한번에 삭제할 수도 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER404", description = "The role does not exist."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.")
    })
    @DeleteMapping("/characters")
    public ApiResponse<Void>  deleteCharacters(@RequestBody CharacterRequest.DeleteCharacters deleteCharacters) {
        characterCommandService.deleteCharacters(deleteCharacters.getCharacterIds());
        return ApiResponse.onSuccess(null);
    }

    @Operation(
            summary = "프로젝트에 해당하는 모든 경로 조회 API | by 노을",
            description = "역할을 생성할 때 프로젝트에 해당하는 모든 path를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/characters/paths")
    public ApiResponse<CharacterResponse.ProjectPathList>  getProjectPaths(@PathVariable("projectId") Long projectId) {
        CharacterResponse.ProjectPathList projectPath = characterQueryService.getProjectPaths(projectId);
        return ApiResponse.onSuccess(projectPath);
    }
}
