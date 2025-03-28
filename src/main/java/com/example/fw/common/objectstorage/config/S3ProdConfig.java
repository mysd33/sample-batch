package com.example.fw.common.objectstorage.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.fw.common.objectstorage.ObjectStorageFileAccessor;
import com.example.fw.common.objectstorage.S3ObjectStorageFileAccessor;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 
 * S3の本番環境用設定クラス
 *
 */
@Profile("production")
@EnableConfigurationProperties({S3ConfigurationProperties.class})
@Configuration
@RequiredArgsConstructor
public class S3ProdConfig {        
    private final S3ConfigurationProperties s3ConfigurationProperties;    
      
    /**
     * オブジェクトストレージアクセスクラス
     */        
    @Bean
    ObjectStorageFileAccessor objectStorageFileAccessor(S3Client s3Client) {
        return new S3ObjectStorageFileAccessor(s3Client, s3ConfigurationProperties.getBucket());
    } 
    
    /**
     * S3クライアント（X-Rayトレースなし）
     */
    @Profile("!xray")
    @Bean
    S3Client s3ClientWithoutXRay() {
        Region region = Region.of(s3ConfigurationProperties.getRegion());
        return S3Client.builder()
                .httpClientBuilder((ApacheHttpClient.builder()))
                .region(region)
                .build();        
    }
    
    /**
     * S3クライアント（X-Rayトレースあり）
     */
    /*
    @Profile("xray")
    @Bean
    S3Client s3ClientWithXRay() {
        Region region = Region.of(s3ConfigurationProperties.getRegion());
        return S3Client.builder()
                .httpClientBuilder((ApacheHttpClient.builder()))
                .region(region)
                // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder().addExecutionInterceptor(new TracingInterceptor()).build())                
                .build();        
    }
    */
}
