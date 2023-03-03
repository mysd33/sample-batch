package com.example.fw.batch.async.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 
 * SQSのプロパティクラス
 *
 */
@Data
@ConfigurationProperties(prefix = "aws.sqs.listener")
public class SQSServerConfigurationProperties {
    private String concurrency;
    private String queueName;
    private boolean ackOnJobStart;
}
