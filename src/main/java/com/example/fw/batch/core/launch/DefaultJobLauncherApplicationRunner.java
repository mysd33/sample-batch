package com.example.fw.batch.core.launch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;

import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * JobLauncherApplicationRunnerのデフォルト実装クラス<br>
 * Spring Bootの標準機能を拡張し実装する。
 */
@Slf4j
public class DefaultJobLauncherApplicationRunner extends JobLauncherApplicationRunner {
    private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);

    public DefaultJobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
            JobRepository jobRepository) {
        super(jobLauncher, jobExplorer, jobRepository);
    }

    @Override
    protected void execute(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        try {
            super.execute(job, jobParameters);
        } catch (JobParametersInvalidException e) {
            // ジョブパラメータ不正エラーが発生したことを明示的にエラーログ出力
            monitoringLogger.error(BatchFrameworkMessageIds.E_FW_BATCH_9001, e, e.getLocalizedMessage());
            // アプリケーション起動に失敗したことを上位に通知するため、そのままリスローする
            throw e;
        }
    }

}
