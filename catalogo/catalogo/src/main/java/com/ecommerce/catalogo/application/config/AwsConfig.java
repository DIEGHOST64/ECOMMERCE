package com.ecommerce.catalogo.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfig {
    
    @Value("${aws.region:us-east-1}")
    private String region;
    
    @Value("${aws.accessKeyId:}")
    private String accessKeyId;
    
    @Value("${aws.secretAccessKey:}")
    private String secretAccessKey;

    @Bean
    public SqsClient sqsClient(){
        if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
            return SqsClient.builder()
                   .region(Region.of(region))
                   .build();
        }
        
        return SqsClient.builder()
               .region(Region.of(region))
               .credentialsProvider(
                       StaticCredentialsProvider.create(
                               AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                       )
               )
                .build();
    }
}
