package com.example.fw.batch.core.config;

import javax.sql.DataSource;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableBatchProcessing
public class SpringBatchConfig extends DefaultBatchConfigurer {
    @Autowired
    private DataSource dataSource;

    /**
     * Bean定義されたジョブをJobRegistryに登録する設定
     * 
     * @param jobRegistry {@link JobRegistry}
     */
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    /**
     * ジョブ管理テーブル群へアクセスするJobRepositoryの設定
     */
    @Override
    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(getTransactionManager());
        // Spring BatchはSERIALIZABLEがデフォルト値
        // 複数のジョブを同時に実行した際にJobRepositoryの更新で例外が発生してしまうため
        // トランザクション分離レベルをREAD_COMMITTEDに設定
        factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

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
