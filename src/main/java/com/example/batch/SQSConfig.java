package com.example.batch;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

/**
 * SQS本番向けの設定クラス
 */
@Configuration
public class SQSConfig {
	@Value("${aws.sqs.concurrency}")
	private String concurrency;

	/**
	 * JMSのメッセージコンバータの定義
	 */
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	/**
	 * JMSListenerContainerFactoryの定義
	 */
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency(concurrency);
		factory.setMessageConverter(jacksonJmsMessageConverter());
		//CLIENT_ACKNOWLEDGEモード：正常終了時のみ確認応答を返しメッセージをSQSから削除
		//エラー時は、SQSにメッセージが残る
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		return factory;
	}

}
