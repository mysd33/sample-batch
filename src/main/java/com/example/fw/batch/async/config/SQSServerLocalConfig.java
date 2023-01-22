package com.example.fw.batch.async.config;

import org.elasticmq.rest.sqs.SQSRestServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.fw.batch.async.sqslocal.ElasticMQLocalExecutor;

/**
 * SQS Local起動の設定クラス(開発時のみ)
 */
@Configuration
@Profile("dev")
public class SQSServerLocalConfig {

    /**
     * ElasticMQの起動クラス
     */
    @Bean
    @ConditionalOnClass(SQSRestServer.class)
    public ElasticMQLocalExecutor elasticMQLocalExecutor() {
        return new ElasticMQLocalExecutor();
    }
}
