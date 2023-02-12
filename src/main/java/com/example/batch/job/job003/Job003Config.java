package com.example.batch.job.job003;

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
public class Job003Config {
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
    public Job job003() {
        // @formatter:off 
        return jobBuilderFactory.get("job003")
                .listener(listener)
                .start(step00301())
                .build();
        // @formatter:on        
    }

    /**
     * Step
     */
    @Bean
    public Step step00301() {
        // @formatter:off         
        return stepBuilderFactory.get("step003_01")
                .tasklet(tasklet003()).
                build();
        // @formatter:on        
    }

    /**
     * Tasklet
     */
    @Bean
    protected Tasklet tasklet003() {
        return new Job003Tasklet();
    }

}
