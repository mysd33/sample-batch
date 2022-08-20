package com.example.batch;

import org.elasticmq.rest.sqs.SQSRestServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.example.fw.batch.sqslocal.ElasticMQLocalExecutor;


/**
 * SQS Local起動の設定クラス(開発時のみ)
 */
@Configuration
@Profile("dev")
@ConditionalOnClass(SQSRestServer.class)
public class SQSLocalConfig {
	private static final String HTTP_LOCALHOST = "http://localhost:";
	private static final String ELASTICMQ = "elasticmq";

	@Value("${aws.sqslocal.port}")
	private String port;

	/**
	 * ElastiqMQ(SQSLocal)起動する場合のSQSConnectionFactoryの定義
	 */
	@Bean
	public SQSConnectionFactory sqsConnectionFactoryLocal() {
		AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration(HTTP_LOCALHOST + port, ELASTICMQ));
		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), builder);
		return connectionFactory;
	}

	/**
	 * ElasticMQの起動クラス
	 */
	@Bean
	public ElasticMQLocalExecutor elasticMQLocalExecutor() {
		return new ElasticMQLocalExecutor();
	}
}
