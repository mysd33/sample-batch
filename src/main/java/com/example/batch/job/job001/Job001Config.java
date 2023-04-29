package com.example.batch.job.job001;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Job001の定義
 */
@Configuration
public class Job001Config {
    /**
     * Job
     */
    @Bean
    public Job job001(JobExecutionListener listener, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        // @formatter:off 
        return new JobBuilder("job001", jobRepository)
                .listener(listener)
                .start(step00101(jobRepository, transactionManager))
                .build();
        // @formatter:on        
    }

    /**
     * Step
     */
    @Bean
    public Step step00101(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        // @formatter:off         
        return new StepBuilder("step001_01", jobRepository)
                .tasklet(tasklet001(), transactionManager)
                .build();
        // @formatter:on        
    }

    /**
     * Tasklet
     */
    @Bean
    protected Tasklet tasklet001() {
        return new Job001Tasklet();
    }

}
