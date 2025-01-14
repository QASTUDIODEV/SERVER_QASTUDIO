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
@RequestMapping("/api/v0/projects")
public class TeamMemberController {

    private final TeamMemberQueryService teamMemberQueryService;

    @Operation(
            summary = "팀원 초대 API | by 노을",
            description = "이메일을 통해 팀원을 프로젝트에 초대합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "잘못된 요청입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"잘못된 요청입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER400", description = "userId와 이메일 정보가 일치하지 않습니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "MEMBER400",
                                    summary = "userId와 이메일 정보가 일치하지 않습니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"MEMBER400\",\n  \"message\": \"userId와 이메일 정보가 일치하지 않습니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"존재하지 않는 프로젝트입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH404", description = "존재하지 않는 사용자입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "AUTH404",
                                    summary = "존재하지 않는 사용자입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"AUTH404\",\n  \"message\": \"존재하지 않는 사용자입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER409", description = "이미 프로젝트에 추가된 유저입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "MEMBER409",
                                    summary = "이미 프로젝트에 추가된 유저입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"MEMBER409\",\n  \"message\": \"이미 프로젝트에 추가된 유저입니다.\"\n}"
                            )
                    ))
    })
    @PostMapping("/team-members/invite")
    public ApiResponse<TeamMemberResponse.MemberList> inviteMembers(@RequestBody @Valid TeamMemberRequest.Invite inviteMembers) {
        List<TeamMemberResponse.Member> members = teamMemberQueryService.inviteMembers(inviteMembers);
        return ApiResponse.onSuccess(TeamMemberConverter.toMemberList(members));
    }

    @Operation(
            summary = "프로젝트에 가입된 팀원 조회 API | by 노을",
            description = "프로젝트에 가입된 팀원 정보를 초대합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"존재하지 않는 프로젝트입니다.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/team-members")
    public ApiResponse<TeamMemberResponse.MemberList> getTeamMemberList(@PathVariable("projectId") Long projectId) {
        List<TeamMemberResponse.Member> members = teamMemberQueryService.getTeamMemberList(projectId);
        return ApiResponse.onSuccess(TeamMemberConverter.toMemberList(members));
    }

    // EmailList
    @Operation(
            summary = "프로젝트 팀원 삭제 API | by 노을",
            description = "프로젝트에 속한 팀원을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "잘못된 요청입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"잘못된 요청입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER400", description = "userId와 이메일 정보가 일치하지 않습니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "MEMBER400",
                                    summary = "userId와 이메일 정보가 일치하지 않습니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"MEMBER400\",\n  \"message\": \"userId와 이메일 정보가 일치하지 않습니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"존재하지 않는 프로젝트입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH404", description = "존재하지 않는 사용자입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "AUTH404",
                                    summary = "존재하지 않는 사용자입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"AUTH404\",\n  \"message\": \"존재하지 않는 사용자입니다.\"\n}"
                            )
                    )),
    })
    @DeleteMapping("/{projectId}/team-members")
    public ApiResponse<Void> deleteMembers(@PathVariable("projectId") Long projectId, @RequestBody @Valid TeamMemberRequest.MemberEmail inviteMember) {
        teamMemberQueryService.deleteMembers(projectId, inviteMember);
        return ApiResponse.onSuccess(null);
    }

    @Operation(
            summary = "팀원 이메일 검색 API | by 노을",
            description = "초대하고자 하는 유저의 이메일을 검색합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "잘못된 요청입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"잘못된 요청입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"존재하지 않는 프로젝트입니다.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/team-members/search")
    public ApiResponse<TeamMemberResponse.UserEmailList> searchMember(@PathVariable("projectId") Long projectId, @RequestParam("email") String email) {
        List<AccountTable> accountTables = teamMemberQueryService.searchMember(projectId, email);
        return ApiResponse.onSuccess(TeamMemberConverter.toUserEmailList(accountTables));
    }

}
