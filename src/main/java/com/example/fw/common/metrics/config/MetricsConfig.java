package com.example.fw.common.metrics.config;

import com.example.fw.common.metrics.MyBatisMetricsObserver;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// メトリクス取得機能のカスタムメトリックス設定クラス
@Configuration
@EnableConfigurationProperties(MetricsConfigurationProperties.class)
public class MetricsConfig {

    /// MyBatisのメトリクス観測用のBean定義
    @Bean
    @ConditionalOnProperty(name = MetricsConfigurationProperties.PROPERTY_PREFIX
        + ".enable", havingValue = "true", matchIfMissing = true)
    MyBatisMetricsObserver myBatisMetricsObserver(ObservationRegistry observationRegistry,
        MetricsConfigurationProperties metricsConfigurationProperties) {
        return new MyBatisMetricsObserver(observationRegistry, metricsConfigurationProperties);
    }

}
