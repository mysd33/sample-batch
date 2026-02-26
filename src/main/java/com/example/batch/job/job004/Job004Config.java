package com.example.batch.job.job004;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.domain.model.User;
import com.example.batch.domain.model.UserTempInfo;

import lombok.RequiredArgsConstructor;

/**
 * Job004の定義<br>
 * Partitioning Stepの例
 */
@Configuration
@RequiredArgsConstructor
public class Job004Config {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    // private final TaskExecutor parallelTaskExecutor;
    private final TaskExecutor contextPropagatingParallelTaskExecutor;
    private final MyBatisCursorItemReader<User> userTableItemReader;
    private final Job004ItemProcessor job004ItemProcessor;
    private final MyBatisBatchItemWriter<UserTempInfo> userTempTableItemWriter;

    @Value("${job004.grid-size:5}")
    private int gridSize;
    @Value("${job004.chunk-size:10}")
    private int chunkSize;

    /**
     * Job
     */
    @Bean
    Job job004Job(JobExecutionListener listener, Step job004StepManager) {
        return new JobBuilder("job004", jobRepository)//
                .listener(listener)//
                .start(job004StepManager)//
                .build();
    }

    /**
     * Manager Step
     */
    @Bean
    Step job004StepManager(PartitionHandler job004PartitionHandler, //
            Job004Partitioner job004Partitioner, //
            Step job004StepWorker) {
        return new StepBuilder("job004StepManager", jobRepository)//
                .partitioner("job004Partitioner", job004Partitioner)//
                .partitionHandler(job004PartitionHandler)//
                .build();
    }

    /**
     * PartiionHandler
     */
    @Bean
    PartitionHandler job004PartitionHandler(Step job004StepWorker) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(job004StepWorker);
        // handler.setTaskExecutor(parallelTaskExecutor);
        handler.setTaskExecutor(contextPropagatingParallelTaskExecutor);
        handler.setGridSize(gridSize);
        return handler;
    }

    /**
     * Worker Step
     */
    @Bean
    Step job004StepWorker() {
        return new StepBuilder("job004StepWorker", jobRepository)//
                .<User, UserTempInfo>chunk(chunkSize, transactionManager)//
                .reader(userTableItemReader)//
                .processor(job004ItemProcessor)//
                .writer(userTempTableItemWriter)//
                .build();
    }
}
