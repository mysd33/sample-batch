package com.example.fw.batch.async.messagelistener;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.example.fw.common.async.model.JobRequest;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.message.FrameworkMessageIds;

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
	private final static ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
	
	private final JobOperator jobOperator;
	/**
	 * キューからジョブの要求情報を受信
	 * 
	 * @param request ジョブの要求情報
	 * @throws JobParametersInvalidException ジョブパラメータが不正
	 */
	@JmsListener(destination =  "${aws.sqs.queue.name}")
	public void onMessage(@Header(JmsHeaders.MESSAGE_ID)  final String messageId, final JobRequest request) 
			throws JobParametersInvalidException {
			String jobId = request.getJobId();
			String jobParameters = request.toParameterString();
			appLogger.info(FrameworkMessageIds.I_FW_0001, messageId,jobId, jobParameters);			
			try {
				Long jobExecutionId = jobOperator.start(jobId, jobParameters);
				appLogger.info(FrameworkMessageIds.I_FW_0002, messageId, jobId, jobExecutionId);			
			} catch (JobInstanceAlreadyExistsException e) {
				appLogger.warn(FrameworkMessageIds.W_FW_8003, messageId, jobId, jobParameters);
			} catch (NoSuchJobException e) {
				//サンプルAPのためキャッチして正常終了しているが、要件によってはthrows句にしてキューからメッセージを消さない
				appLogger.warn(FrameworkMessageIds.W_FW_8004, messageId, jobId);
			}
	}
}