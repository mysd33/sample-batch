package com.example.fw.batch.async.config;

import org.elasticmq.rest.sqs.SQSRestServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.fw.batch.async.sqslocal.ElasticMQLocalExecutor;
import com.example.fw.common.async.config.SQSCommonConfigurationProperties;

import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * SQS Local起動の設定クラス(開発時のみ)
 */
@Profile("dev")
@Configuration
@EnableConfigurationProperties({SQSCommonConfigurationProperties.class, SQSServerConfigurationProperties.class})
public class SQSServerLocalConfig {
        
    /**
     * ElasticMQの起動クラス
     */
    @Bean
    @ConditionalOnClass(SQSRestServer.class)
    ElasticMQLocalExecutor elasticMQLocalExecutor(SqsClient sqsClient,             
            SQSCommonConfigurationProperties sqsCommonConfigurationProperties,
            SQSServerConfigurationProperties sqsServerConfigurationProperties) {            
        return new ElasticMQLocalExecutor(sqsClient,                 
                sqsCommonConfigurationProperties.getSqslocal().getPort(),
                sqsServerConfigurationProperties.getQueueName()
                );
    }
}
