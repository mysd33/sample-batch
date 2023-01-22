package com.example.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.batch.domain.common.record.TodoRecord;
import com.example.fw.batch.async.messagelistener.AsyncMessageListener;
import com.example.fw.batch.async.store.DefaultJmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.async.store.JmsMessageStore;
import com.example.fw.batch.async.store.ThreadLocalJmsMessageStore;
import com.example.fw.batch.exception.DefaultExceptionHandler;
import com.example.fw.batch.exception.ExceptionHandler;
import com.example.fw.batch.listener.DefaultJobExecutionListener;
import com.example.fw.common.message.CommonFrameworkMessageIds;

/**
 * 
 * SpringBatch設定クラス
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer {
    @Autowired
    private DataSource dataSource;

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

    /**
     * SQSからメッセージを取得するMessageListener
     * 
     * @param jobOperator {@link JobOperator}
     */
    @Bean
    public AsyncMessageListener asyncMessageListener(JobOperator jobOperator, JmsMessageManager jmsMessageManager) {
        return new AsyncMessageListener(jobOperator, jmsMessageManager);
    }

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
    public JobExecutionListener defaultJobExecutionListener() {
        return new DefaultJobExecutionListener();
    }

    /**
     * 集約例外ハンドリングクラス
     */
    @Bean
    public ExceptionHandler defaultExceptionHandler() {
        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();
        defaultExceptionHandler.setDefaultExceptionMessageId(CommonFrameworkMessageIds.E_CM_FW_9001);
        return defaultExceptionHandler;
    }

    /**
     * TodoListの読み込みクラス
     * 
     * @param filePathName ファイルパス名
     */
    @StepScope
    @Bean
    public FlatFileItemReader<TodoRecord> todoListFileReader(
            @Value("#{jobExecutionContext['input.file.name']}") String filePathName) {
        return new FlatFileItemReaderBuilder<TodoRecord>().name("todoListReader")
                .resource(new FileSystemResource(filePathName)).delimited().delimiter(",").names("todoTitle")
                .targetType(TodoRecord.class).encoding("UTF-8").build();
    }

}
