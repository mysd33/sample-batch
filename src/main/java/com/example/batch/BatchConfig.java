package com.example.batch;

import javax.sql.DataSource;

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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.batch.domain.common.record.TodoRecord;
import com.example.batch.domain.common.record.TodoRecordFieldSetMapper;
import com.example.fw.batch.async.listener.AsyncMessageListener;

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

	@Bean
	public AsyncMessageListener asyncMessageListener(JobOperator jobOperator) {
		return new AsyncMessageListener(jobOperator);
	}

	/**
	 * Bean定義されたジョブをJobRegistryに登録する設定
	 * 
	 * @param jobRegistry JobRegistry
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
	 * TodoListの読み込みクラス
	 */
	@StepScope
	@Bean
	public FlatFileItemReader<TodoRecord> todoListFileReader(
			@Value("#{jobExecutionContext['input.file.name']}") String filename) {		
		return new FlatFileItemReaderBuilder<TodoRecord>()
				.name("todoListReader")
				.resource(new FileSystemResource(filename))
				.lineTokenizer(new DelimitedLineTokenizer())
				.fieldSetMapper(new TodoRecordFieldSetMapper())				
				.encoding("UTF-8")
				.build();
	}

}
