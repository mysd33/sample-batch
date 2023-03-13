package com.example.fw.batch.async.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.example.fw.batch.async.messaging.AsyncMessageListener;
import com.example.fw.batch.store.JmsMessageManager;
import com.example.fw.common.async.config.SQSCommonConfigurationProperties;

/**
 * SQSのサーバ側設定クラス
 */
@Configuration
@EnableConfigurationProperties({ SQSCommonConfigurationProperties.class, SQSServerConfigurationProperties.class })
public class SQSServerConfig {    

    /**
     * JMSListenerContainerFactoryの定義
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory,
            MessageConverter jacksonJmsMessageConverter,
            SQSServerConfigurationProperties sqsServerConfigurationProperties) {        
        DefaultJmsListenerContainerFactory factory;        
        if (sqsServerConfigurationProperties.isAckOnJobStart()) {
            factory = new DefaultJmsListenerContainerFactory() {
                @Override
                protected DefaultMessageListenerContainer createContainerInstance() {
                    return new DefaultMessageListenerContainer() {
                        @Override
                        protected void commitIfNecessary(Session session, Message message) throws JMSException {
                            // ackOnJobStart=trueの時は、DefaultJobExecutionListenerで明示的にacknowledgeするため
                            //　何もしない（ただし、エラー時など、acknowledge漏れに注意）
                        }
                    };
                }
            };
        } else {
            factory = new DefaultJmsListenerContainerFactory();
        }
        factory.setReceiveTimeout(sqsServerConfigurationProperties.getReceiveTimeout());
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency(sqsServerConfigurationProperties.getConcurrency());
        factory.setMessageConverter(jacksonJmsMessageConverter);
        // CLIENT_ACKNOWLEDGEモード：正常終了時のみ確認応答を返しメッセージをSQSから削除
        // エラー時は、SQSにメッセージが残る
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    /**
     * SQSからメッセージを取得するMessageListener
     * 
     * @param jobOperator {@link JobOperator}
     */
    @Bean
    public AsyncMessageListener asyncMessageListener(JobOperator jobOperator, JmsMessageManager jmsMessageManager,
            SQSServerConfigurationProperties sqsServerConfigurationProperties) {
        return new AsyncMessageListener(jobOperator, jmsMessageManager, sqsServerConfigurationProperties);
    }
    
}
