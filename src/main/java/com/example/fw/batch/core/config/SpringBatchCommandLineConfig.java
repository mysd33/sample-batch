package com.example.fw.batch.core.config;

import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.batch.autoconfigure.BatchProperties;
import org.springframework.boot.batch.autoconfigure.JobLauncherApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.core.launch.DefaultJobLauncherApplicationRunner;
import com.example.fw.batch.core.listener.CommandLineJobExecutionListener;
import com.example.fw.batch.jobflow.config.JobflowConfigurationProperties;
import com.example.fw.batch.jobflow.converter.SfnJobParametersConverter;

import lombok.RequiredArgsConstructor;

/// SpringBatchのコマンドライン実行用の設定クラス
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({ SpringBatchConfigurationProperties.class, JobflowConfigurationProperties.class })
@ConditionalOnProperty(prefix = SpringBatchConfigurationProperties.PROPERTY_PREFIX, name = "type", havingValue = "commandline")
public class SpringBatchCommandLineConfig {

    /// ジョブパラメータのコンバータの定義（ジョブフロー未使用）
    @Bean
    @ConditionalOnProperty(prefix = JobflowConfigurationProperties.PROPERTY_PREFIX, name = "enable", havingValue = "false", matchIfMissing = false)
    JobParametersConverter jobParametersConverterNotUsingJobflow() {
        return new DefaultJobParametersConverter();
    }

    /// ジョブフローによるコマンド起動用のJobParametersConverterの定義
    @Bean
    @ConditionalOnProperty(prefix = JobflowConfigurationProperties.PROPERTY_PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
    JobParametersConverter jobParametersConverterForJobFlow() {
        return new SfnJobParametersConverter();
    }

    /// コマンドライン実行用のJobLauncherApplicationRunnerのBean定義<br>
    @Bean
    JobLauncherApplicationRunner jobLauncherApplicationRunner(JobOperator jobOperator, BatchProperties properties,
            JobRepository jobRepository, JobflowConfigurationProperties jobflowConfigurationProperties,
            Environment env) {
        var runner = new DefaultJobLauncherApplicationRunner(jobOperator, jobRepository,
                jobflowConfigurationProperties, env);
        String jobName = properties.getJob().getName();
        if (StringUtils.hasText(jobName)) {
            runner.setJobName(jobName);
        }
        return runner;
    }

    /// ジョブの実行に関わる例外ハンドリング、ログ出力機能の設定
    @Bean
    JobExecutionListener defaultJobExecutionListenerForCommandLine(ExceptionHandler defaultExceptionHandler) {
        return new CommandLineJobExecutionListener(defaultExceptionHandler);
    }
}
