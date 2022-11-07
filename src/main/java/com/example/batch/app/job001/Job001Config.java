package com.example.batch.app.job001;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.batch.domain.job001.Job001Tasklet;

/**
 * Job001の定義
 */
@Configuration
public class Job001Config {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	//TODO:
	//@Qualifier("defaultJobExecutionListener")
	//@Autowired
	//private JobExecutionListener listener;

	/**
	 * Job
	 */
	@Bean
	public Job job001() {
		return jobBuilderFactory.get("job001")
				.start(step001_01())
				.build();
	}

	/**
	 * Step
	 */
	@Bean
	public Step step001_01() {
		return stepBuilderFactory.get("step001_01")
				.tasklet(tasklet001())
				.build();
	}

	/**
	 * Tasklet
	 */
	@Bean
	protected Tasklet tasklet001() {
		return new Job001Tasklet();
	}

}
