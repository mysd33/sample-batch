package com.example.batch.job.job003;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

/**
 * Job001の定義
 */
@Configuration
@RequiredArgsConstructor
public class Job003Config {
    private final Job003Tasklet job003Tasklet;

    /**
     * Job
     */
    @Bean
    Job job003(JobExecutionListener listener, JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new JobBuilder("job003", jobRepository)//
                .listener(listener)//
                .start(step00301(jobRepository, transactionManager))//
                .build();
    }

    /**
     * Step
     */
    @Bean
    Step step00301(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step003_01", jobRepository)//
                .tasklet(job003Tasklet, transactionManager)//
                .build();
    }

}
