package qastudio.backend.global.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import qastudio.backend.global.s3.dto.AwsDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AwsDTO.PresignedUrlUploadResponse getPresignedUrlToUpload(AwsDTO.PresignedUploadRequest presignedUploadRequest) {

        /// 제한시간 설정
        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(3); // 3 Minute
        expiration.setTime(expTime);

        String keyName = UUID.randomUUID() + "_" + presignedUploadRequest.getFileName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        String key = generatePresignedUrlRequest.getKey();

        return AwsDTO.PresignedUrlUploadResponse.builder()
                .url(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString())
                .keyName(key)
                .build();
    }

    public Boolean isExistKeyName(String keyName) {
        return amazonS3.doesObjectExist(bucket, keyName);
    }

    public AwsDTO.PresignedUrlUploadResponseList getPresignedUrlToUploadList(AwsDTO.PresignedUploadListRequest presignedUploadListRequest) {

        /// 제한시간 설정
        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(3); // 3 Minute
        expiration.setTime(expTime);



        List<AwsDTO.PresignedUrlUploadResponse> responses = presignedUploadListRequest.getFileNameList().stream()
                .map(oldKeyName -> {
                    String keyName = UUID.randomUUID() + "_" + oldKeyName;

                    GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(expiration);

                    String key = generatePresignedUrlRequest.getKey();

                    return AwsDTO.PresignedUrlUploadResponse.builder()
                            .url(amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString())
                            .keyName(key)
                            .build();
                })
                .collect(Collectors.toList());

        return AwsDTO.PresignedUrlUploadResponseList.builder().presignedUrlUploadResponses(responses).build();
    }

    public String generateStaticUrl(String keyName) {
        return "https://" + bucket + ".s3." + amazonS3.getRegionName() + ".amazonaws.com/" + keyName;
    }
}