package com.example.fw.batch.core.config;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.core.listener.DefaultJobExecutionListener;

/**
 * 
 * SpringBatchの設定クラス
 *
 */
@Configuration
public class SpringBatchConfig {
    /**
     * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定（ディレードでの非同期実行）
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.batch", name = "type", havingValue = "async", matchIfMissing = true)
    JobExecutionListener defaultJobExecutionListenerForAsync(JmsMessageManager jmsMessageManager,
            ExceptionHandler defaultExceptionHandler,
            SQSServerConfigurationProperties sqsServerConfigurationProperties) {
        return new DefaultJobExecutionListener(defaultExceptionHandler, jmsMessageManager,
                sqsServerConfigurationProperties);
    }

    /**
     * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定（純バッチ）
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.batch", name = "type", havingValue = "standard")
    JobExecutionListener defaultJobExecutionListenerForStandard(ExceptionHandler defaultExceptionHandler) {
        return new DefaultJobExecutionListener(defaultExceptionHandler, null, null);
    }

}
