package qastudio.backend.domain.project.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public class TeamMemberRequest {

    @Getter
    public static class Invite {
        // project pk
        @NotNull(message = "projectId는 필수 입력 값입니다.")
        private Long projectId;
        // 이메일 리스트
        @NotEmpty(message = "emailList 는 필수 입력 값입니다.")
        private List<@Valid MemberEmail> memberEmailList;
    }

    @Getter
    public static class MemberEmail {
        // 이메일
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
    }

    @Getter
    public static class InviteWithEmail {
        // 이메일
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
        // projectId
        @NotNull(message = "projectId는 필수 입력 값입니다.")
        private Long projectId;
    }

}
