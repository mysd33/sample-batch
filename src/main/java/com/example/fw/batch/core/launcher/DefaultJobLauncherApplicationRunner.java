package com.example.fw.batch.core.launcher;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.boot.batch.autoconfigure.JobLauncherApplicationRunner;

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

    public DefaultJobLauncherApplicationRunner(JobOperator jobOperator) {
        super(jobOperator);
    }

    @Override
    protected void execute(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, InvalidJobParametersException {
        try {
            super.execute(job, jobParameters);
        } catch (InvalidJobParametersException e) {
            // ジョブパラメータ不正エラーが発生したことを明示的にエラーログ出力
            monitoringLogger.error(BatchFrameworkMessageIds.E_FW_BATCH_9001, e, e.getLocalizedMessage());
            // アプリケーション起動に失敗したことを上位に通知するため、そのままリスローする
            throw e;
        }
    }

}
