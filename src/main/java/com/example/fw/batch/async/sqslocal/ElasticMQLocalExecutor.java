package com.example.fw.batch.async.sqslocal;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;

/**
 * SQS互換のAPIを持つElasticMQをローカル起動するクラス
 */
@Slf4j
@RequiredArgsConstructor
public class ElasticMQLocalExecutor {
    private static final String LOCAL_HOST = "localhost";
    private final SqsClient sqsClient;
    private final int port;    
    private final String queueName;

    private SQSRestServer server;
    private String queueUrl;       
    
    /**
     * ElasticMQ 起動
     * 
     * @throws Exception
     */
    @PostConstruct
    public void startup() throws Exception {
        server = SQSRestServerBuilder.withPort(port).withInterface(LOCAL_HOST).start();
        log.info("ElasticMQ start");
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();
        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        queueUrl = createQueueResponse.queueUrl();
        log.info("ElasticMQ queueUrl:" + queueUrl);
    }

    /**
     * ElasticMQ 終了
     * 
     * @throws Exception
     */
    @PreDestroy
    public void shutdown() {
        if (server != null) {
            DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                    .queueUrl(queueUrl).build();
            sqsClient.deleteQueue(deleteQueueRequest);
            
            server.stopAndWait();
        }
    }
}
