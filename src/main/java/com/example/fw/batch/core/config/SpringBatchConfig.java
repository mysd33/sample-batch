package com.example.fw.batch.core.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.batch.autoconfigure.BatchProperties;
import org.springframework.boot.batch.autoconfigure.JobLauncherApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import lombok.RequiredArgsConstructor;

/**
 * 
 * SpringBatchの設定クラス
 *
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SpringBatchConfigurationProperties.class)
// JDBCベースのインフラを有効化
@EnableJdbcJobRepository
public class SpringBatchConfig {
    //  @formatter:off
    // 
    // Spring Bootのスレッドプールに任せない場合は、下記のTaskExecutor Bean定義を有効化する
    //private final SpringBatchConfigurationProperties springBatchConfigurationProperties;
    /**
     * VirtualThread有効時のPartitioning Step（多重実行）用のTaskExecutor
     * 
     */
    
    /*
    @Bean
    @ConditionalOnProperty(prefix = "spring.threads.virtual", name = "enabled", havingValue = "true", matchIfMissing = false)
    TaskExecutor parallelVirtualThreadTaskExecutor() {
        return new VirtualThreadTaskExecutor(springBatchConfigurationProperties.getThreadNamePrefix());
    }*/

    /**
     * VirtualThread無効時のPartitioning Step（多重実行）用のTheadPool版TaskExecutor
     * 
     */
    /*
    @Bean
    @ConditionalOnProperty(prefix = "spring.threads.virtual", name = "enabled", havingValue = "false", matchIfMissing = true)
    TaskExecutor parallelThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(springBatchConfigurationProperties.getThreadCorePoolSize());
        executor.setMaxPoolSize(springBatchConfigurationProperties.getThreadMaxPoolSize());
        executor.setQueueCapacity(springBatchConfigurationProperties.getQueueCapacity());
        return executor;
    }*/
    //@formatter:on

    /**
     * ジョブパラメータのコンバータの定義
     */
    @Bean
    JobParametersConverter jobParametersConverter() {
        return new DefaultJobParametersConverter();
    }

    /**
     * ジョブレジストリの定義
     */
    @Bean
    JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    /**
     * キューを介した非同期処理依頼メッセージによるバッチ実行のSpring Batch設定
     */
    @Configuration
    @ConditionalOnProperty(prefix = SpringBatchConfigurationProperties.PROPERTY_PREFIX, name = "type", havingValue = "async", matchIfMissing = true)
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
    @ConditionalOnProperty(prefix = SpringBatchConfigurationProperties.PROPERTY_PREFIX, name = "type", havingValue = "commandline")
    static class CommandLineSpringBatchConfig {
        /**
         * コマンドライン実行用のJobLauncherApplicationRunnerのBean定義<br>
         */
        @Bean
        JobLauncherApplicationRunner jobLauncherApplicationRunner(JobOperator jobOperator, BatchProperties properties) {
            DefaultJobLauncherApplicationRunner runner = new DefaultJobLauncherApplicationRunner(jobOperator);
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
