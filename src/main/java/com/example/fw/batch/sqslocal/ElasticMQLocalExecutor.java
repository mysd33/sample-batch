package com.example.fw.batch.sqslocal;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * SQS互換のAPIを持つElasticMQをローカル起動するクラス
 */
@Slf4j
public class ElasticMQLocalExecutor {
	private static final String ELASTICMQ = "elasticmq";
	private static final String HTTP_LOCALHOST = "http://localhost:";
	private static final String LOCAL_HOST = "localhost";
	private SQSRestServer server;
	private String queueUrl;
	private AmazonSQS amazonSQS;
	
	@Value("${aws.sqslocal.port}")
	private String port;

	@Value("${aws.sqs.queue.name}")
	private String queueName;
	
	
	/**
	 * ElasticMQ 起動
	 * @throws Exception
	 */
	@PostConstruct
	public void startup() throws Exception {		
		server = SQSRestServerBuilder.withPort(Integer.valueOf(port)).withInterface(LOCAL_HOST).start();
		log.info("ElasticMQ start");
		
		amazonSQS = AmazonSQSClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration(HTTP_LOCALHOST + port, ELASTICMQ))
				.build();
		queueUrl = amazonSQS.createQueue(queueName).getQueueUrl();
		log.info("ElasticMQ queueUrl:" + queueUrl);
	}

	/**
	 * ElasticMQ 終了
	 * @throws Exception
	 */
	@PreDestroy
	public void shutdown() throws Exception {
		if (server != null) {
			amazonSQS.deleteQueue(queueUrl);
			server.stopAndWait();
		}
	}
}
