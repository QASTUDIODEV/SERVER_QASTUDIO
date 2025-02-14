package qastudio.backend.domain.project.dto.response;

import lombok.*;
import qastudio.backend.domain.project.entity.enums.Role;

import java.util.List;

public class TeamMemberResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserEmail {
        // 유저 이메일
        private Long userId;
        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserEmailList {
        private List<UserEmail> userEmails;
        private List<String> unacceptedMembers;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Member {
        // 유저 pk
        private Long userId;
        // 유저 역할
        private Role projectRole;
        // 유저 이메일
        private String email;
        // 유저 닉네임
        private String nickname;
        // 유저 프로필 이미지
        private String profileImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberList {
        // 멤버 리스트
        private List<Member> members;
        // 초대를 수락하지 않은 이메일
        private List<String> unacceptedMembers;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AcceptInvitation {
        private Long projectId;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SearchEmail {
        private boolean isQastudioMember;
    }

}
