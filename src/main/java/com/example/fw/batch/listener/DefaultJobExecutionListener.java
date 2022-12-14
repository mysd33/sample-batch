package com.example.fw.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.exception.ExceptionHandler;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * キューからメッセージ削除、例外ハンドリング、ログ出力を行うJobExecutionListener
 */
@Slf4j
public class DefaultJobExecutionListener implements JobExecutionListener {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

	// ジョブ開始後即時にキューメッセージをACK（削除）するかどうか
	// 長時間バッチではSQSの可視性タイムアウトを短くするためtrueにするとよい
	@Value("${aws.sqs.ackOnJobStart:false}")
	private boolean ackOnJobStart;

	@Autowired
	private JmsMessageManager jmsMessageManager;

	@Autowired
	private ExceptionHandler defaultExceptionHandler;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0003, jobExecution.getJobId(), jobExecution.getId());
		if (ackOnJobStart) {
			// ACKしキューからメッセージを削除
			jmsMessageManager.acknowledge();
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		long elapsedTime = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
		appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0004, elapsedTime, jobExecution.getJobId(),
				jobExecution.getId(), jobExecution.getExitStatus().getExitCode());
		// 例外発生時に集約例外ハンドリング
		defaultExceptionHandler.handle(jobExecution);
	}
}
