package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.converter.TeamMemberConverter;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.service.TeamMemberQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/team-member")
public class TeamMemberController {

    private final TeamMemberQueryService teamMemberQueryService;

    @Operation(
            summary = "팀원 초대 API",
            description = "이메일을 통해 팀원을 프로젝트에 초대합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/invite/{projectId}")
    public ApiResponse<TeamMemberResponse.MemberList> inviteMembers(@PathVariable("projectId") Long projectId, @RequestBody @Valid TeamMemberRequest.EmailList inviteMembers) {
        List<TeamMemberResponse.Member> members = teamMemberQueryService.inviteMembers(projectId, inviteMembers);
        return ApiResponse.onSuccess(TeamMemberConverter.toMemberList(members));
    }

    @Operation(
            summary = "프로젝트 팀원 조회 API",
            description = "프로젝트에 속한 팀원 정보를 초대합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
    })
    @GetMapping("/{projectId}")
    public ApiResponse<TeamMemberResponse.MemberList> getTeamMemberList(@PathVariable("projectId") Long projectId) {
        List<TeamMemberResponse.Member> members = teamMemberQueryService.getTeamMemberList(projectId);
        return ApiResponse.onSuccess(TeamMemberConverter.toMemberList(members));
    }

    // EmailList
    @Operation(
            summary = "프로젝트 팀원 삭제 API",
            description = "프로젝트에 속한 팀원을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @DeleteMapping("/{projectId}")
    public ApiResponse<TeamMemberResponse.MemberList> deleteMembers(@PathVariable("projectId") Long projectId, @RequestBody @Valid TeamMemberRequest.EmailList inviteMembers) {
        List<TeamMemberResponse.Member> members = teamMemberQueryService.deleteMembers(projectId, inviteMembers);
        return ApiResponse.onSuccess(TeamMemberConverter.toMemberList(members));
    }

    @Operation(
            summary = "팀원 이메일 검색 API",
            description = "초대하고자 하는 유저의 이메일을 검색합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/{projectId}/search")
    public ApiResponse<TeamMemberResponse.UserEmailList> searchMember(@PathVariable("projectId") Long projectId, @RequestParam("email") String email) {
        // 주의 - 프로젝트에 초대되지 않은 사람을 응답해야 함
        List<AccountTable> accountTables = teamMemberQueryService.searchMember(projectId, email);
        return ApiResponse.onSuccess(TeamMemberConverter.toUserEmailList(accountTables));
    }

}
