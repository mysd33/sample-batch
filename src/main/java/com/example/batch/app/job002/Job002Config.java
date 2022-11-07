package com.example.batch.app.job002;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.batch.domain.common.record.TodoRecord;
import com.example.batch.domain.job002.Job002ItemProcessor;
import com.example.fw.batch.writer.NoOpItemWriter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class Job002Config {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	//TODO:
	//@Qualifier("defaultJobExecutionListener")
	//@Autowired
	//private JobExecutionListener listener;
	
	@Qualifier("todoListFileReader")
	@Autowired
	private FlatFileItemReader<TodoRecord> todoListFileReader;

	// TODO:チャンクサイズの設定ファイル化
	@Value("${job002.chunkSize:5}")
	private int chunkSize;

	/**
	 * Job
	 */
	@Bean
	public Job job002() {
		return jobBuilderFactory.get("job002")
				.start(step002_01())
				.next(step002_02())
				.build();
	}

	/**
	 * Step1 パラメータから入出力ファイルパスを取得する前処理
	 */
	@Bean
	public Step step002_01() {
		return stepBuilderFactory.get("step002_01").tasklet((contribution, chunkContext) -> {
			StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
			String param01 = stepExecution.getJobParameters().getString("param01");
			String param02 = stepExecution.getJobParameters().getString("param02");
			//TODO: ApplicationLogger化
			log.debug("param01:{}, param02:{}", param01, param02);
			ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
			jobExecutionContext.put("input.file.name","files/input/todolist.csv");			
			return RepeatStatus.FINISHED;
		}).build();
	}

	/**
	 * Step2 本処理
	 */
	@Bean
	public Step step002_02() {
		return stepBuilderFactory.get("step002_02")
				.<TodoRecord, TodoRecord>chunk(chunkSize)
				.reader(todoListFileReader)
				.processor(processor002())
				.writer(noOpItemWriter())
				.build();
	}

	/**
	 * ItemProcessor
	 */
	@Bean
	public ItemProcessor<TodoRecord, TodoRecord> processor002() {
		return new Job002ItemProcessor();
	}
	
	/**
	 * 何もしないItemWriter
	 */
	@Bean
	public ItemWriter<TodoRecord> noOpItemWriter() {
		return new NoOpItemWriter<>();
	}
	
	
}
