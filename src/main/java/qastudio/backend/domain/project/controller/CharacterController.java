package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.service.CharacterQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/character")
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
}
