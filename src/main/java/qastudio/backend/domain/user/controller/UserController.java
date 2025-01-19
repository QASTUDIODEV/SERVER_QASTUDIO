package qastudio.backend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.user.converter.UserConverter;
import qastudio.backend.domain.user.dto.request.UserRequest;
import qastudio.backend.domain.user.dto.response.UserResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.service.UserCommandService;
import qastudio.backend.domain.user.service.UserQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.global.handler.annotation.Auth;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/users")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Operation(
            summary = "사용자 정보 조회 API | by 제로",
            description = "마이페이지의 사용자 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "존재하지 않는 사용자입니다.")
    })
    @GetMapping("")
    public ApiResponse<UserResponse.UserInfo> getUser(@Auth Long userId) {
        User user = userQueryService.getUser(userId);
        Integer projectCnt = userQueryService.getProjectCount(userId);
        return ApiResponse.onSuccess(UserConverter.toUserInfo(user, projectCnt));
    }

    @Operation(
            summary = "사용자 정보 수정 API | by 제로",
            description = "마이페이지의 사용자 정보를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "존재하지 않는 사용자입니다.")
    })
    @PatchMapping("")
    public ApiResponse<UserResponse.UserInfo> updateUser(
            @Auth Long userId,
            @RequestBody @Valid UserRequest.UpdateUserInfo updateUserInfo) {
        User updatedUser = userCommandService.updateProfile(userId, updateUserInfo);
        Integer projectCnt = userQueryService.getProjectCount(userId);
        return ApiResponse.onSuccess(UserConverter.toUserInfo(updatedUser, projectCnt));
    }

    @Operation(
            summary = "사용자 프로젝트 리스트 조회 API | by 제로",
            description = "마이페이지의 프로젝트 리스트를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "존재하지 않는 사용자입니다.")
    })
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호, 0번이 1 페이지입니다.")
    })
    @GetMapping("/projects")
    public ApiResponse<UserResponse.UserProjectList> getUserProjectList(
            @Auth Long userId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        Page<UserProject> userProjectList = userQueryService.getUserProjectList(userId, page);
        return ApiResponse.onSuccess(UserConverter.toUserProjectList(userProjectList));
    }
}
