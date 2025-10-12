package com.example.fw.batch.core.config;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.async.store.DefaultJmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageStore;
import com.example.fw.batch.async.store.ThreadLocalJmsMessageStore;
import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.core.launcher.DefaultJobLauncherApplicationRunner;
import com.example.fw.batch.core.listener.DefaultJobExecutionListener;
import com.example.fw.common.constants.FrameworkConstants;

/**
 * 
 * SpringBatchの設定クラス
 *
 */
@Configuration
public class SpringBatchConfig {

    // Spring Batchのプロパティプレフィックス
    public static final String PROPERTY_PREFIX = FrameworkConstants.PROPERTY_BASE_NAME + "batch";

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
         * コマンドライン実行用のJobLauncherApplicationRunnerのBean定義<br>
         */
        @Bean
        JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
                JobRepository jobRepository, BatchProperties properties) {
            DefaultJobLauncherApplicationRunner runner = new DefaultJobLauncherApplicationRunner(jobLauncher,
                    jobExplorer, jobRepository);
            String jobName = properties.getJob().getName();
            if (StringUtils.hasText(jobName)) {
                runner.setJobName(jobName);
            }
            return runner;
        }

        /**
         * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定
         */
        @Bean
        JobExecutionListener defaultJobExecutionListenerForCommandLine(ExceptionHandler defaultExceptionHandler) {
            return new DefaultJobExecutionListener(defaultExceptionHandler, null, null);
        }
    }

}
