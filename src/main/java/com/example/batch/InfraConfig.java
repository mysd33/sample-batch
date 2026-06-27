package com.example.batch;

import com.example.batch.infra.httpclient.WebClientResponseErrorHandler;
import com.example.fw.batch.aspect.LogAspect;
import com.example.fw.common.httpclient.config.WebClientConfigPackage;
import com.example.fw.common.logging.config.LoggingConfigPackage;
import com.example.fw.common.metrics.config.MetricsConfigPackage;
import com.example.fw.common.objectstorage.config.S3ConfigPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/// インフラストラクチャ層の設定クラス
@Configuration
// RESTクライアント機能、オブジェクトストレージアクセス機能、ロギング拡張機能の設定、メトリックス転送機能の設定を追加
@ComponentScan(basePackageClasses = {WebClientConfigPackage.class, S3ConfigPackage.class,
    LoggingConfigPackage.class, MetricsConfigPackage.class})
public class InfraConfig {

    /// WebClientでのエラーハンドラークラス
    @Bean
    WebClientResponseErrorHandler webClientResponseErrorHandler() {
        return new WebClientResponseErrorHandler();
    }

    /// Repositoryの性能ログ用のロギングクラス
    @Bean
    LogAspect logAspect() {
        return new LogAspect();
    }

    /// RestTemplateの設定
//    @Bean
//    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
//        // ログ出力クラスの設定
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//        interceptors.add(new RestTemplateLoggingInterceptor());
//        return restTemplateBuilder
//                // エラーハンドラークラスの設定
//                .errorHandler(new RestTemplateResponseErrorHandler()).interceptors(interceptors).build();
//    }

}
