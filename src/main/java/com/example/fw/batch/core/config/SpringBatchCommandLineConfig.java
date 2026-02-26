package com.example.fw.batch.core.config;

import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.batch.autoconfigure.BatchProperties;
import org.springframework.boot.batch.autoconfigure.JobLauncherApplicationRunner;
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
     * ジョブパラメータのコンバータの定義
     */
    @Bean
    JobParametersConverter jobParametersConverter() {
        return new DefaultJobParametersConverter();
    }

    /**
     * コマンドライン実行用のJobLauncherApplicationRunnerのBean定義<br>
     */
    @Bean
    JobLauncherApplicationRunner jobLauncherApplicationRunner(JobOperator jobOperator, BatchProperties properties) {
        DefaultJobLauncherApplicationRunner runner = new DefaultJobLauncherApplicationRunner(jobOperator);
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
