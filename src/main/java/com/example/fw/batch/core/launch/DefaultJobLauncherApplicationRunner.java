package com.example.fw.batch.core.launch;

import java.util.Objects;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.core.env.Environment;

import com.example.fw.batch.core.config.SpringBatchConfigurationProperties;
import com.example.fw.batch.jobflow.sfn.SfnTaskResultSender;
import com.example.fw.batch.jobflow.sfn.service.SfnTaskResultPersistService;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * JobLauncherApplicationRunnerのデフォルト実装クラス<br>
 * Spring Bootの標準機能を拡張し実装する。
 */
@Slf4j
public class DefaultJobLauncherApplicationRunner extends JobLauncherApplicationRunner {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);
    private final JobRepository jobRepository;
    private final SpringBatchConfigurationProperties springBatchConfigurationProperties;
    private final Environment env;
    private SfnTaskResultPersistService sfnTaskResultPersistService = null;
    private SfnTaskResultSender sfnTaskResultSender = null;

    /**
     * コンストラクタ
     * 
     * @param jobLauncher
     * @param jobExplorer
     * @param jobRepository
     * @param springBatchConfigurationProperties
     * @param env
     */
    public DefaultJobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
            JobRepository jobRepository, SpringBatchConfigurationProperties springBatchConfigurationProperties,
            Environment env) {
        super(jobLauncher, jobExplorer, jobRepository);
        this.jobRepository = jobRepository;
        this.springBatchConfigurationProperties = springBatchConfigurationProperties;
        this.env = env;
    }

    @Autowired(required = false)
    public void setSfnTaskResultPersistService(SfnTaskResultPersistService sfnTaskResultPersistService) {
        this.sfnTaskResultPersistService = sfnTaskResultPersistService;
    }

    @Autowired(required = false)
    public void setSfnTaskResultSender(SfnTaskResultSender sfnTaskResultSender) {
        this.sfnTaskResultSender = sfnTaskResultSender;
    }

    @Override
    protected void execute(Job job, JobParameters jobParameters)
            throws JobExecutionAlreadyRunningException, JobRestartException, JobParametersInvalidException {
        try {
            super.execute(job, jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            // ジョブインスタンスが既に完了している場合は警告ログを出力し本処理をスキップし、正常終了させる
            JobInstance jobInstance = jobRepository.getJobInstance(job.getName(), jobParameters);
            Long jobInstanceId = Objects.requireNonNull(jobInstance).getId();
            appLogger.warn(BatchFrameworkMessageIds.W_FW_BTCTRL_8001, job.getName(), jobInstanceId);
            if (sfnTaskResultPersistService == null) {
                return;
            }
            // 前回実行時のStep Functionsへ送信した処理結果が存在する場合、
            String previousResult = sfnTaskResultPersistService.findTaskResultById(jobInstanceId);
            // タスクトークンを取得
            String taskToken = env.getProperty(springBatchConfigurationProperties.getTaskTokenEnvName());
            if (previousResult != null && sfnTaskResultSender != null) {
                // StepFunctionsに処理結果を再度送信して後続ジョブの実行を継続させる
                sfnTaskResultSender.resendTaskSuccessByJsonString(jobInstanceId, previousResult, taskToken);
            }
        } catch (JobParametersInvalidException e) {
            // ジョブパラメータ不正エラーが発生したことを明示的にエラーログ出力
            monitoringLogger.error(BatchFrameworkMessageIds.E_FW_BATCH_9001, e, e.getLocalizedMessage());
            // アプリケーション起動に失敗したことを上位に通知するため、そのままリスローする
            throw e;
        }
    }

}
