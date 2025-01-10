package qastudio.backend.domain.project.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

public class TeamMemberRequest {

    @Getter
    public static class EmailList {
        // 이메일 리스트
        @NotEmpty(message = "emailList 는 필수 입력 값입니다.")
        private List<@Email(message = "이메일 형식에 맞지 않습니다.")
                     @NotBlank(message = "이메일은 필수 입력 값입니다.") String> emailList;
    }

    @Getter
    public static class UserEmail {
        // 유저 이메일
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
    }
}
