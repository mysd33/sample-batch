package com.example.fw.batch.core.config;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.async.store.DefaultJmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageStore;
import com.example.fw.batch.async.store.ThreadLocalJmsMessageStore;
import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.core.listener.DefaultJobExecutionListener;
import com.example.fw.common.constants.FrameworkConstants;

/**
 * 
 * SpringBatchの設定クラス
 *
 */
@Configuration
public class SpringBatchConfig {
    // TODO: プロパティ名の見直しを予定
    // Spring Batchのプロパティプレフィックス
    public static final String PROPERTY_PREFIX = FrameworkConstants.PROPERTY_BASE_NAME + "spring.batch";

    /**
     * キューを介した非同期処理依頼メッセージによるバッチ実行のSpring Batch設定
     */
    @Configuration
    @ConditionalOnProperty(prefix = PROPERTY_PREFIX, name = "type", havingValue = "async", matchIfMissing = true)
    static class AsyncSpringBatchConfig {
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

    /**
     * コマンドライン実行のSpring Batch設定
     */
    @Configuration
    @ConditionalOnProperty(prefix = PROPERTY_PREFIX, name = "type", havingValue = "commandline")
    static class CommandLineSpringBatchConfig {
        /**
         * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定
         */
        @Bean
        JobExecutionListener defaultJobExecutionListenerForCommandLine(ExceptionHandler defaultExceptionHandler) {
            return new DefaultJobExecutionListener(defaultExceptionHandler, null, null);
        }
    }

}
