package com.yazen.cornellmarketplace.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    private S3Client s3Client;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile uploadedFile) {
        String key = UUID.randomUUID() + "-" + uploadedFile.getOriginalFilename();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromBytes(uploadedFile.getBytes()));
        } catch (java.io.IOException e) {
            System.out.println("File not found.");
        }
        return "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + key;
    }
}
