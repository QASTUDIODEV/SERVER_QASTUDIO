package qastudio.backend.domain.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import qastudio.backend.domain.user.entity.enums.EmailType;

import java.time.LocalDate;
import java.util.List;

public class UserResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserProfile {
        // 닉네임
        private String nickname;
        // 프로필 이미지
        private String profileImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfo {
        // 유저 pk
        private Long userId;
        // 닉네임
        private String nickname;
        // 이메일
        private String email;
        // 프로필 이미지
        private String profileImage;
        // 배너 이미지
        private String bannerImage;
        // 소셜 계정
        private List<EmailType> account;
        // 진행 중인 프로젝트 개수
        private Integer projectCnt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberInfo {
        // 유저 pk
        private Long userId;
        // 닉네임
        private String nickname;
        // 이메일
        private String email;
        // 프로필 이미지
        private String profileImage;
        // 배너 이미지
        private String bannerImage;
        // 진행 중인 프로젝트 개수
        private Integer projectCnt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserProject {
        // 프로젝트 pk
        private Long projectId;
        // 프로젝트 이름
        private String projectName;
        // 프로젝트 대표 이미지
        private String projectImage;
        // 참여자 수
        private Integer participant;
        // 마지막 수정일자
        private LocalDate lastModifiedDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserProjectList {
        // 프로젝트 리스트
        private List<UserProject> userProjectList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }
}
