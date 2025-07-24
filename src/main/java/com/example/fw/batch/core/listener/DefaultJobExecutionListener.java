package com.example.fw.batch.core.listener;

import java.time.LocalDateTime;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.core.exception.ExceptionHandler;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.systemdate.SystemDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * キューからメッセージ削除、例外ハンドリング、ログ出力を行うJobExecutionListener
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultJobExecutionListener implements JobExecutionListener {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final ExceptionHandler defaultExceptionHandler;
    private final JmsMessageManager jmsMessageManager;
    private final SQSServerConfigurationProperties sqsServerConfigurationProperties;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0003, jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobId(), jobExecution.getId());
        // isAckOnJobStartがtrueの場合、ジョブ開始後即時にキューメッセージをACK（削除）する
        // 長時間バッチではSQSの可視性タイムアウトを短くするためtrueにするとよい
        if (jmsMessageManager != null && //
                sqsServerConfigurationProperties != null && sqsServerConfigurationProperties.isAckOnJobStart()) {
            // ACKしキューからメッセージを削除
            jmsMessageManager.acknowledge();
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime startTime = jobExecution.getStartTime();
        LocalDateTime endTime = jobExecution.getEndTime();
        double elapsedTime = 0D;
        if (startTime != null && endTime != null) {
            elapsedTime = SystemDateUtils.calcElapsedTimeByMilliSeconds(startTime, endTime);
        }
        appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0004, elapsedTime, startTime,
                jobExecution.getJobInstance().getJobName(), jobExecution.getJobId(), jobExecution.getId(),
                jobExecution.getExitStatus().getExitCode());

        // 例外発生時に集約例外ハンドリング
        defaultExceptionHandler.handle(jobExecution);
    }
}
