package com.example.fw.batch.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

/**
 * SpringBatchのキューを介した非同期処理依頼、コマンドライン実行の両方共通の設定クラス
 */
@Configuration
public class SpringBatchCommonConfig {
    /**
     * ContextPropagatingTaskDecoratorをBean定義することで、
     * スレッド間でTraceIDの値を伝播させるTaskExecutorになる<br>
     * （参考）
     * https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot?utm_source=chatgpt.com#beware-the-context
     */
    @Bean
    ContextPropagatingTaskDecorator contextPropagatingTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

}
