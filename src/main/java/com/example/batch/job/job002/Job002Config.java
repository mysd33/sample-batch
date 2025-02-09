package com.example.batch.job.job002;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.domain.record.TodoRecord;
import com.example.fw.batch.writer.NoOpItemWriter;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Job002Config {
    private static ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final Job002ItemProcessor job002ItemProcessor;
    @Qualifier("todoListFileReader")
    private final FlatFileItemReader<TodoRecord> todoListFileReader;

    @Value("${job002.chunk-size:5}")
    private int chunkSize;

    /**
     * Job
     */
    @Bean
    Job job002(JobExecutionListener listener, JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new JobBuilder("job002", jobRepository)//
                .listener(listener)//
                .start(step00201(jobRepository, transactionManager))//
                .next(step00202(jobRepository, transactionManager))//
                .build();
    }

    /**
     * Step1 パラメータから入出力ファイルパスを取得する前処理
     */
    @Bean
    Step step00201(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step002_01", jobRepository)//
                .tasklet((contribution, chunkContext) -> {
                    StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                    String param01 = stepExecution.getJobParameters().getString("param01");
                    String param02 = stepExecution.getJobParameters().getString("param02");
                    appLogger.debug("param01:{}, param02:{}", param01, param02);
                    ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
                    jobExecutionContext.put("input.file.name", "files/input/todolist.csv");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    /**
     * Step2 本処理
     */
    @Bean
    Step step00202(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step002_02", jobRepository)//
                .<TodoRecord, TodoRecord>chunk(chunkSize, transactionManager)//
                .reader(todoListFileReader)//
                .processor(job002ItemProcessor)//
                .writer(noOpItemWriter())//
                .build();
    }

    /**
     * 何もしないItemWriter
     */
    @Bean
    ItemWriter<TodoRecord> noOpItemWriter() {
        return new NoOpItemWriter<>();
    }

}
