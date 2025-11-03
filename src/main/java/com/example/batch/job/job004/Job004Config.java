package com.example.batch.job.job004;

import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.domain.model.User;
import com.example.fw.batch.core.writer.NoOpItemWriter;

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
    private final TaskExecutor parallelTaskExecutor;
    private final MyBatisCursorItemReader<User> userTableItemReader;
    private final Job004ItemProcessor job004ItemProcessor;

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
    Step job004StepManager(PartitionHandler partitionHandler, //
            Job004Partitioner job004Partitioner, //
            Step job004StepWorker) {
        return new StepBuilder("job004StepManager", jobRepository)//
                .partitioner(" job004Partitioner", job004Partitioner)//
                .partitionHandler(partitionHandler)//
                .build();
    }

    /**
     * PartiionHandler
     */
    @Bean
    PartitionHandler job004PartitionHandler(@Value("${grid.size:5}") int gridSize, Step job004StepWorker) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(job004StepWorker);
        handler.setTaskExecutor(parallelTaskExecutor);
        handler.setGridSize(gridSize);
        return handler;
    }

    /**
     * Step Worker
     */
    @Bean
    Step job004StepWorker() {
        return new StepBuilder("job004StepWorker", jobRepository)//
                .<User, User>chunk(10, transactionManager)//
                .reader(userTableItemReader)//
                .processor(job004ItemProcessor)//
                .writer(job004NoOpItemWriter())//
                .build();
    }

    /**
     * TODO: MyBatisBatchItemWriterのサンプルを実装するように修正予定
     */
    @Bean
    ItemWriter<User> job004NoOpItemWriter() {
        return new NoOpItemWriter<>();
    }
}
