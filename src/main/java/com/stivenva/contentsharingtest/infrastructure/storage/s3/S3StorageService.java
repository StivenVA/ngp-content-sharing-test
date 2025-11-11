package com.stivenva.contentsharingtest.infrastructure.storage.s3;

import com.stivenva.contentsharingtest.domain.port.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@RequiredArgsConstructor
@Service
public class S3StorageService implements StorageService {
    
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.public-base-url:}")
    private String publicBaseUrl;
    
    @Override
    public String upload(byte[] bytes, String fileName,String contentType) {
        if (bytes == null || fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("bytes and fileName are required");
        }

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();
        s3Client.putObject(putReq, RequestBody.fromBytes(bytes));
        return buildPublicUrl(fileName);
    }

    @Override
    public byte[] download(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is required");
        }
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();
        ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(getReq);
        return bytes.asByteArray();
    }

    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();
        s3Client.deleteObject(delReq);
    }

    private String buildPublicUrl(String key) {
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
            return base + "/" + key;
        }
        String region = s3Client.serviceClientConfiguration().region() == null ? "us-east-1" : s3Client.serviceClientConfiguration().region().id();
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
