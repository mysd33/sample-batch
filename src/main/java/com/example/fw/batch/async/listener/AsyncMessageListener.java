package com.example.fw.batch.async.listener;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.example.fw.common.async.model.JobRequest;

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
			//TODO: メッセージ定義、ロギング機能に置き換え
			log.info("ジョブ実行依頼受信[MessageId:{}][JobId:{}][JobParameter:{}]", messageId,jobId, jobParameters);			
			try {
				Long jobExecutionId = jobOperator.start(jobId, jobParameters);
				log.info("ジョブ実行完了[MessageId:{}][JobId:{}][JobExecutionId:{}]", messageId, jobId, jobExecutionId);			
			} catch (JobInstanceAlreadyExistsException e) {
				log.warn("すでにこのジョブは実行されています。[SQS MessageId:{}][Jobid:{}][JobParameter:{}]", messageId, jobId, jobParameters);
			} catch (NoSuchJobException e) {
				//サンプルAPのためキャッチして正常終了しているが、要件によってはthrows句にしてキューからメッセージを消さない
				log.warn("該当のジョブIDはありません。[SQS MessageId:{}][JobId:{}]", messageId, jobId);
			}
	}
}