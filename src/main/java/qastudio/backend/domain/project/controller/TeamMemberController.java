package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.converter.TeamMemberConverter;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.TeamMemberResponse;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.service.TeamMemberCommandService;
import qastudio.backend.domain.project.service.TeamMemberQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/projects")
public class TeamMemberController {

    private final TeamMemberQueryService teamMemberQueryService;
    private final TeamMemberCommandService teamMemberCommandService;

    @Operation(
            summary = "팀원 초대 API | by 노을",
            description = "이메일을 통해 팀원을 프로젝트에 초대합니다. 이메일을 통해 받은 토큰 값으로 팀원이 초대에 수락합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER400", description = "User ID and email do not match.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "MEMBER400",
                                    summary = "User ID and email do not match.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"MEMBER400\",\n  \"message\": \"User ID and email do not match.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH404", description = "User not found.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "AUTH404",
                                    summary = "User not found.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"AUTH404\",\n  \"message\": \"User not found.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER409", description = "The user is already added to the project.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "MEMBER409",
                                    summary = "The user is already added to the project.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"MEMBER409\",\n  \"message\": \"The user is already added to the project.\"\n}"
                            )
                    ))
    })
    @PostMapping("/team-members/invite")
    public ApiResponse<Void> inviteMembers(@RequestBody @Valid TeamMemberRequest.Invite inviteMembers) {
        teamMemberQueryService.inviteMembers(inviteMembers.getProjectId(), inviteMembers.getMemberEmailList());
        return ApiResponse.onSuccess(null);
    }

    @Operation(
            summary = "프로젝트에 가입된 팀원 조회 API | by 노을",
            description = "프로젝트에 가입된 팀원 정보를 조회합니다. LEADER, MEMBER 모두 조회하며 사용자의 프로필, 닉네임 등 상세 정보를 확인할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/team-members")
    public ApiResponse<TeamMemberResponse.MemberList> getTeamMemberList(@PathVariable("projectId") Long projectId) {
        List<UserProject> userProjects = teamMemberQueryService.getTeamMemberList(projectId);
        return ApiResponse.onSuccess(TeamMemberConverter.toMemberList(userProjects));
    }

    @Operation(
            summary = "프로젝트 팀원 초대 시 현재 가입된 팀원의 이메일 조회 API | by 노을",
            description = "프로젝트 팀원 초대 시 현재 가입된 팀원의 이메일을 조회하기 위해 사용합니다. LEADER는 조회하지 않으며, 이메일과 userId만 응답합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/team-members/emails")
    public ApiResponse<TeamMemberResponse.UserEmailList> getTeamMemberExceptLeader(@PathVariable("projectId") Long projectId) {
        List<UserProject> userProjects = teamMemberQueryService.getTeamMemberExceptLeader(projectId);
        return ApiResponse.onSuccess(TeamMemberConverter.toUserEmailListFromUserProjects(userProjects));
    }

    // EmailList
    @Operation(
            summary = "프로젝트 팀원 삭제 API | by 노을",
            description = "프로젝트에 속한 팀원을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER400", description = "User ID and email do not match.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "MEMBER400",
                                    summary = "User ID and email do not match.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"MEMBER400\",\n  \"message\": \"User ID and email do not match.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH404", description = "User not found.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "AUTH404",
                                    summary = "User not found.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"AUTH404\",\n  \"message\": \"User not found.\"\n}"
                            )
                    )),
    })
    @DeleteMapping("/{projectId}/team-members")
    public ApiResponse<Void> deleteMembers(@PathVariable("projectId") Long projectId, @RequestBody @Valid TeamMemberRequest.MemberEmail inviteMember) {
        teamMemberCommandService.deleteMembers(projectId, inviteMember);
        return ApiResponse.onSuccess(null);
    }

    @Operation(
            summary = "팀원 이메일 검색 API | by 노을",
            description = "초대하고자 하는 유저가 프로젝트에 가입되어있는지 boolean 값으로 응답합니다. true이면 이미 가입된 유저, false이면 가입되지 않은 유저입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/team-members/search")
    public ApiResponse<TeamMemberResponse.SearchEmail> searchMember(@PathVariable("projectId") Long projectId, @RequestParam("email") String email) {
        boolean isMember = teamMemberQueryService.searchMember(projectId, email);
        return ApiResponse.onSuccess(TeamMemberConverter.toSearchEmail(isMember));
    }

    @Operation(
            summary = "팀원 초대 수락 API | by 노을",
            description = "토큰을 입력하여 초대를 수락합니다."
    )
    @GetMapping("/team-members/invite")
    public ApiResponse<TeamMemberResponse.AcceptInvitation> acceptInvitation(@RequestParam("token") String token) {
        Long projectId = teamMemberCommandService.inviteMember(token);
        return ApiResponse.onSuccess(TeamMemberConverter.toAcceptInvitation(projectId));
    }
}
