package com.example.fw.batch.core.config;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.exception.ExceptionHandler;
import com.example.fw.batch.listener.DefaultJobExecutionListener;
import com.example.fw.batch.store.DefaultJmsMessageManager;
import com.example.fw.batch.store.JmsMessageManager;
import com.example.fw.batch.store.JmsMessageStore;
import com.example.fw.batch.store.ThreadLocalJmsMessageStore;

/**
 * 
 * SpringBatchの設定クラス
 *
 */
@Configuration
public class SpringBatchConfig {
    /**
     * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定
     */
    @Bean
    public JobExecutionListener defaultJobExecutionListener(JmsMessageManager jmsMessageManager,
            ExceptionHandler defaultExceptionHandler,
            SQSServerConfigurationProperties sqsServerConfigurationProperties) {
        return new DefaultJobExecutionListener(jmsMessageManager, defaultExceptionHandler,
                sqsServerConfigurationProperties);
    }
    
    /**
     * JMSのメッセージストアクラス
     */
    @Bean
    public JmsMessageStore jmsMessageStore() {
        return new ThreadLocalJmsMessageStore();
    }

    /**
     * JMSのメッセージ管理クラス
     */
    @Bean
    public JmsMessageManager jmsMessageManager(JmsMessageStore jmsMessageStore) {
        return new DefaultJmsMessageManager(jmsMessageStore);
    }

}
