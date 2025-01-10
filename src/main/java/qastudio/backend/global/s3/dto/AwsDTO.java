package qastudio.backend.global.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class AwsDTO {

    @Schema(description = "AWS S3 URL 응답 정보 리스트")
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PresignedUrlUploadResponseList {
        private List<PresignedUrlUploadResponse> presignedUrlUploadResponses;
    }

    @Schema(description = "AWS S3 URL 응답 정보")
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PresignedUrlUploadResponse {
        private String url;
        private String keyName;
    }

    @Getter
    public static class PresignedUploadListRequest {
        private List<String> fileNameList;
    }

    @Getter
    public static class PresignedUploadRequest {
        private String fileName;
    }
}