package com.example.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.batch.infra.httpclient.RestTemplateResponseErrorHandler;
import com.example.batch.infra.httpclient.WebClientResponseErrorHandler;
import com.example.fw.common.httpclient.RestTemplateLoggingInterceptor;
import com.example.fw.common.httpclient.WebClientLoggingFilter;
import com.example.fw.common.objectstorage.config.S3ConfigPackage;

/**
 * 
 * インフラストラクチャ層の設定クラス
 *
 */
@Configuration
@ComponentScan(basePackageClasses = { S3ConfigPackage.class })
public class InfraConfig {

    /**
     * 
     * WebClientクラス
     * 
     */
    @Bean
    public WebClient webClientWithoutXRay(WebClientLoggingFilter loggingFilter) {
        return WebClient.builder().filter(loggingFilter.filter()).build();
    }

    /**
     * WebClientでのログ出力クラス
     */
    @Bean
    public WebClientLoggingFilter webClientLoggingFilter() {
        return new WebClientLoggingFilter();
    }

    /**
     * WebClientでのエラーハンドラークラス
     */
    @Bean
    public WebClientResponseErrorHandler webClientResponseErrorHandler() {
        return new WebClientResponseErrorHandler();
    }

    /**
     * RestTemplateの設定
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        // ログ出力クラスの設定
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new RestTemplateLoggingInterceptor());
        return restTemplateBuilder
                // エラーハンドラークラスの設定
                .errorHandler(new RestTemplateResponseErrorHandler()).interceptors(interceptors).build();
    }

}
