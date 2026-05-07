package com.example.fw.common.objectstorage.config;

import com.example.fw.common.objectstorage.BucketCreateInitializer;
import com.example.fw.common.objectstorage.ObjectStorageFileAccessor;
import com.example.fw.common.objectstorage.S3ObjectStorageFileAccessor;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/// S3が開発環境上でのローカルサーバFake（LocalStack）実行に置き換える設定クラス<br>
@Profile("dev")
@ConditionalOnProperty(prefix = S3ConfigurationProperties.LOCALFAKE_PROPERTY_PREFIX, name = "type", havingValue = "localstack")
@EnableConfigurationProperties({S3ConfigurationProperties.class})
@Configuration
@RequiredArgsConstructor
public class S3LocalLocalStackFakeConfig {

    private final S3ConfigurationProperties s3ConfigurationProperties;

    /// オブジェクトストレージアクセスクラス
    @Bean
    ObjectStorageFileAccessor objectStorageFileAccessor(S3Client s3Client) {
        return new S3ObjectStorageFileAccessor(s3Client, s3ConfigurationProperties.getBucket());
    }

    /// S3クライアント
    @Bean
    S3Client s3Client() {
        // ダミーのクレデンシャル
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
            s3ConfigurationProperties.getLocalfake().getAccessKeyId(),
            s3ConfigurationProperties.getLocalfake().getSecretAccessKey());

        Region region = Region.of(s3ConfigurationProperties.getRegion());
        // @formatter:off
        return S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .region(region)       
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(URI.create("http://localhost:" + s3ConfigurationProperties.getLocalfake().getPort()))
                //Path-Styleの設定
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true).build())
                .build();        
        // @formatter:on
    }

    /// バケット初期作成クラス
    @Bean
    BucketCreateInitializer bucketCreateInitializer(S3Client s3Client) {
        return new BucketCreateInitializer(s3Client, s3ConfigurationProperties.getBucket());
    }

}
