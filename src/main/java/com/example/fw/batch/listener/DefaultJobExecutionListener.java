package com.example.fw.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.fw.batch.exception.ExceptionHandler;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.message.FrameworkMessageIds;

import lombok.extern.slf4j.Slf4j;

/**
 * ジョブ実行にかかわる例外ハンドリング、ログ出力を行うJobExecutionListener
 */
@Slf4j
public class DefaultJobExecutionListener implements JobExecutionListener {
	private static final ApplicationLogger appLoger = LoggerFactory.getApplicationLogger(log);
	
	@Autowired
	private ExceptionHandler defaultExceptionHandler;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		appLoger.info(FrameworkMessageIds.I_FW_0004, jobExecution.getJobId(), jobExecution.getId());

		//TODO: メッセージをACKし、キュー（SQS）からメッセージ削除
		
	}

	@Override
	public void afterJob(JobExecution jobExecution) {		
		long elapsedTime = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
		appLoger.info(FrameworkMessageIds.I_FW_0005, elapsedTime, jobExecution.getJobId(), jobExecution.getId(), jobExecution.getExitStatus().getExitCode());
		//例外発生時に集約例外ハンドリング
		defaultExceptionHandler.handle(jobExecution);		
	}
}
