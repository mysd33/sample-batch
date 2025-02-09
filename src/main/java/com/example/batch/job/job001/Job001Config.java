package com.example.batch.job.job001;

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
public class Job001Config {
    private final Job001Tasklet job001Tasklet;

    /**
     * Job
     */
    @Bean
    Job job001(JobExecutionListener listener, JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new JobBuilder("job001", jobRepository)//
                .listener(listener)//
                .start(step00101(jobRepository, transactionManager))//
                .build();
    }

    /**
     * Step
     */
    @Bean
    Step step00101(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step001_01", jobRepository)//
                .tasklet(job001Tasklet, transactionManager)//
                .build();
    }

}
