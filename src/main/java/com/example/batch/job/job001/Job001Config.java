package com.example.batch.job.job001;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Job001の定義
 */
@Configuration
public class Job001Config {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobExecutionListener listener;

    /**
     * Job
     */
    @Bean
    public Job job001() {
        // @formatter:off 
        return jobBuilderFactory.get("job001")
                .listener(listener)
                .start(step00101())
                .build();
        // @formatter:on        
    }

    /**
     * Step
     */
    @Bean
    public Step step00101() {
        // @formatter:off         
        return stepBuilderFactory.get("step001_01")
                .tasklet(tasklet001()).
                build();
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
