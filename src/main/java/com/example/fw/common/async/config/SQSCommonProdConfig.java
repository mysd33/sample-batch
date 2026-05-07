package com.example.fw.common.async.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/// SQS本番向けの設定クラス
@Profile("production")
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({SQSCommonConfigurationProperties.class})
public class SQSCommonProdConfig {

    private final SQSCommonConfigurationProperties sqsCommonConfigurationProperties;

    /// SQSClientの定義
    @Bean
    SqsClient sqsClient() {
        Region region = Region.of(sqsCommonConfigurationProperties.getRegion());
        return SqsClient.builder()//
            .httpClientBuilder(ApacheHttpClient.builder())//
            .region(region)//
            .build();
    }
}
