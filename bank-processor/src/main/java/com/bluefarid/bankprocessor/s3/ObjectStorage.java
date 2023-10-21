package com.bluefarid.bankprocessor.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ObjectStorage {
    private final S3Client s3Client;

    public byte[] getFile(String bucketName, String key) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(request).readAllBytes();
    }

}
