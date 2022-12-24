package com.example.fw.batch.async.messagelistener;

import java.util.Map;

import javax.jms.Message;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.async.model.JobRequest;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * キューを監視しジョブを実行するクラス
 *
 */
@Slf4j
@RequiredArgsConstructor
public class AsyncMessageListener {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
	private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);	
	private final JobOperator jobOperator;
	private final JmsMessageManager jmsMessageManager;
	/**
	 * キューからジョブの要求情報を受信
	 * 
	 * @param request ジョブの要求情報
	 * 
	 */
	@JmsListener(destination = "${aws.sqs.queue.name}")
	public void onMessage(@Headers final Map<String, String> headers, Message message, final JobRequest request) {
		// メッセージが有効な形式かチェック
		if (!request.isValid()) {
			monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9001, null, request);
			return;
		}
		//メッセージID取得
		String messageId = headers.get(JmsHeaders.MESSAGE_ID);
		//メッセージをJmsMessageManagerの管理下におく
		jmsMessageManager.manage(message);
		
		Long jobExecutionId = null;
		if (request.isRestart()) {
			// ジョブ再実行の場合
			Long preExecutionId = request.getJobExecutionId();			
			try {
				// SpringBatchで前回ジョブ実行IDを用いてジョブ再実行
				jobExecutionId = jobOperator.restart(preExecutionId);
			} catch (NoSuchJobExecutionException e) {
				appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8003, e, messageId, preExecutionId);
			} catch (NoSuchJobException e) {
				appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8004, e, messageId, preExecutionId);				
			} catch (JobInstanceAlreadyCompleteException e) {
				appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8005, e, messageId, preExecutionId);				
			} catch (JobParametersInvalidException e) {
				monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9003, e, messageId, preExecutionId);				
			} catch (JobRestartException e) {
				monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9004, e, messageId, preExecutionId);				
			}
			return;
		}
		// ジョブ初回実行の場合
		String jobId = request.getJobId();
		String jobParameters = request.toParameterString();
		try {
			appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0001, messageId, jobId, jobParameters);
			// SpringBatchでジョブ実行
			jobExecutionId = jobOperator.start(jobId, jobParameters);
			appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0002, messageId, jobId, jobExecutionId);
		} catch (JobInstanceAlreadyExistsException e) {
			appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8001, e, messageId, jobId, jobParameters);
		} catch (NoSuchJobException e) {
			appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8002, e, messageId, jobId);
		} catch (JobParametersInvalidException e) {
			monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9002, e, messageId, jobId);
		}
	}
}