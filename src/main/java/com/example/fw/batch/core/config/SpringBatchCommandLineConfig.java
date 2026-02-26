package com.example.fw.batch.core.config;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.core.launch.DefaultJobLauncherApplicationRunner;
import com.example.fw.batch.core.listener.CommandLineJobExecutionListener;

import lombok.RequiredArgsConstructor;

/**
 * 
 * SpringBatchのコマンドライン実行用の設定クラス
 *
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SpringBatchConfigurationProperties.class)
@ConditionalOnProperty(prefix = SpringBatchConfigurationProperties.PROPERTY_PREFIX, name = "type", havingValue = "commandline")
public class SpringBatchCommandLineConfig {
    /**
     * コマンドライン実行用のJobLauncherApplicationRunnerのBean定義<br>
     */
    @Bean
    JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
            JobRepository jobRepository, BatchProperties properties) {
        DefaultJobLauncherApplicationRunner runner = new DefaultJobLauncherApplicationRunner(jobLauncher, jobExplorer,
                jobRepository);
        String jobName = properties.getJob().getName();
        if (StringUtils.hasText(jobName)) {
            runner.setJobName(jobName);
        }
        return runner;
    }

    /**
     * ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定
     */
    @Bean
    JobExecutionListener defaultJobExecutionListenerForCommandLine(ExceptionHandler defaultExceptionHandler) {
        return new CommandLineJobExecutionListener(defaultExceptionHandler);
    }
}
