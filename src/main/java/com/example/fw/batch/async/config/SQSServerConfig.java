package com.example.fw.batch.async.config;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.example.fw.common.async.config.SQSCommonConfigurationProperties;

/**
 * SQSのサーバ側設定クラス
 */
@Configuration
@EnableConfigurationProperties({SQSCommonConfigurationProperties.class, SQSServerConfigurationProperties.class})
public class SQSServerConfig {

    /**
     * JMSListenerContainerFactoryの定義
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory,
            MessageConverter jacksonJmsMessageConverter, 
            SQSServerConfigurationProperties sqsServerConfigurationProperties
            ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency(sqsServerConfigurationProperties.getConcurrency());
        factory.setMessageConverter(jacksonJmsMessageConverter);
        // CLIENT_ACKNOWLEDGEモード：正常終了時のみ確認応答を返しメッセージをSQSから削除
        // エラー時は、SQSにメッセージが残る
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

}
