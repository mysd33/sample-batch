package com.example.fw.batch.core.config;

import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.async.store.DefaultJmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageStore;
import com.example.fw.batch.async.store.ThreadLocalJmsMessageStore;
import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.core.listener.DefaultJobExecutionListener;

import lombok.RequiredArgsConstructor;

/**
 * 
 * キューを介した非同期処理依頼メッセージによるSpringBatchの設定クラス<br>
 * デフォルトの設定クラス
 *
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SpringBatchConfigurationProperties.class)
@ConditionalOnProperty(prefix = SpringBatchConfigurationProperties.PROPERTY_PREFIX, name = "type", havingValue = "async", matchIfMissing = true)
public class SpringBatchAsyncConfig {

    /**
     * ジョブパラメータのコンバータの定義
     */
    @Bean
    JobParametersConverter jobParametersConverter() {
        return new DefaultJobParametersConverter();
    }

    /**
     * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定
     */
    @Bean
    JobExecutionListener defaultJobExecutionListenerForAsync(JmsMessageManager jmsMessageManager,
            ExceptionHandler defaultExceptionHandler,
            SQSServerConfigurationProperties sqsServerConfigurationProperties) {
        return new DefaultJobExecutionListener(defaultExceptionHandler, jmsMessageManager,
                sqsServerConfigurationProperties);
    }

    /**
     * JMSのメッセージストアクラス
     */
    @Bean
    JmsMessageStore jmsMessageStore() {
        return new ThreadLocalJmsMessageStore();
    }

    /**
     * JMSのメッセージ管理クラス
     */
    @Bean
    JmsMessageManager jmsMessageManager(JmsMessageStore jmsMessageStore) {
        return new DefaultJmsMessageManager(jmsMessageStore);
    }

}
