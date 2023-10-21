package com.bluefarid.bankprocessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create("1f587d9d-0f73-44b0-a244-eb65a38b9fb9",
                        "ea8f9014e2dba906a1e1d16ced994f0ddb567bee05386ccfb13646e58d41263d"))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("https://s3.ir-thr-at1.arvanstorage.ir"))
                .build();
    }
}
