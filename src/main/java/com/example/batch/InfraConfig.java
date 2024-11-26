package com.example.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.batch.infra.httpclient.WebClientResponseErrorHandler;
import com.example.fw.batch.aspect.LogAspect;
import com.example.fw.common.httpclient.config.WebClientConfigPackage;
import com.example.fw.common.objectstorage.config.S3ConfigPackage;

/**
 * 
 * インフラストラクチャ層の設定クラス
 *
 */
@Configuration
@ComponentScan(basePackageClasses = { WebClientConfigPackage.class, S3ConfigPackage.class })
public class InfraConfig {

    /**
     * WebClientでのエラーハンドラークラス
     */
    @Bean
    public WebClientResponseErrorHandler webClientResponseErrorHandler() {
        return new WebClientResponseErrorHandler();
    }
    
    /**
     * Repositoryの性能ログ用のロギングクラス
     */
    @Bean
    public LogAspect logAspect() {
    	return new LogAspect();
    }

    /**
     * RestTemplateの設定
     */
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
//        // ログ出力クラスの設定
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//        interceptors.add(new RestTemplateLoggingInterceptor());
//        return restTemplateBuilder
//                // エラーハンドラークラスの設定
//                .errorHandler(new RestTemplateResponseErrorHandler()).interceptors(interceptors).build();
//    }

}
