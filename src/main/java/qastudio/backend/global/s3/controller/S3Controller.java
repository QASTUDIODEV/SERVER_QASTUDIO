package qastudio.backend.global.s3.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.global.s3.dto.AwsDTO;
import qastudio.backend.global.s3.service.S3Service;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/s3")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "Upload용 Presigned URL 생성 | by 제이미", description = "업로드를 위한 Presigned URL을 생성한다")
    @PostMapping("/presigned/upload")
    public ApiResponse<AwsDTO.PresignedUrlUploadResponse> getPresignedUrlToUpload(@RequestBody AwsDTO.PresignedUploadRequest presignedUploadRequest) {
        AwsDTO.PresignedUrlUploadResponse presignedUrlToUpload = s3Service.getPresignedUrlToUpload(presignedUploadRequest);
        return ApiResponse.onSuccess(presignedUrlToUpload);
    }

    @Operation(summary = "여러 개 Upload용 Presigned URL 생성 | by 제이미", description = "업로드를 위한 Presigned URL를 여러 개 생성한다")
    @PostMapping("/presigned/upload/list")
    public ApiResponse<?> getPresignedUrlToUploadList(@RequestBody AwsDTO.PresignedUploadListRequest presignedUploadListRequest) {
        AwsDTO.PresignedUrlUploadResponseList presignedUrlToUploadList = s3Service.getPresignedUrlToUploadList(presignedUploadListRequest);
        return ApiResponse.onSuccess(presignedUrlToUploadList);
    }
}
