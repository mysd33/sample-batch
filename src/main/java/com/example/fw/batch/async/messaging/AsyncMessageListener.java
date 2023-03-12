package com.example.fw.batch.async.messaging;

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

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.batch.store.JmsMessageManager;
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
    private final SQSServerConfigurationProperties sqsServerConfigurationProperties;

    /**
     * キューからジョブの要求情報を受信
     * 
     * @param request ジョブの要求情報
     * 
     */
    @JmsListener(destination = "${aws.sqs.listener.queue-name}")
    public void onMessage(@Headers final Map<String, String> headers, Message message, final JobRequest request) {
        // メッセージが有効な形式かチェック
        if (!request.isValid()) {
            monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9001, null, request);
            return;
        }
        // メッセージID取得
        String messageId = headers.get(JmsHeaders.MESSAGE_ID);
        // メッセージをJmsMessageManagerの管理下におく
        jmsMessageManager.manage(message);

        Long jobExecutionId = null;
        if (request.isRestart()) {
            // ジョブ再実行の場合
            Long preExecutionId = request.getJobExecutionId();
            try {
                // SpringBatchで前回ジョブ実行IDを用いてジョブ再実行
                appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0005, messageId, preExecutionId);
                jobExecutionId = jobOperator.restart(preExecutionId);
                appLogger.info(BatchFrameworkMessageIds.I_BT_FW_0006, messageId, preExecutionId, jobExecutionId);                
            } catch (NoSuchJobExecutionException e) {
                appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8003, e, messageId, preExecutionId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
            } catch (NoSuchJobException e) {
                appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8004, e, messageId, preExecutionId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
            } catch (JobInstanceAlreadyCompleteException e) {
                appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8005, e, messageId, preExecutionId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
            } catch (JobParametersInvalidException e) {
                monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9003, e, messageId, preExecutionId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
            } catch (JobRestartException e) {
                monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9004, e, messageId, preExecutionId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
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
            acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
        } catch (NoSuchJobException e) {
            appLogger.warn(BatchFrameworkMessageIds.W_BT_FW_8002, e, messageId, jobId);
            acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
        } catch (JobParametersInvalidException e) {
            monitoringLogger.error(BatchFrameworkMessageIds.E_BT_FW_9002, e, messageId, jobId);
            acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
        }
    }
    
    private void acknowledgeExplicitlyOnExceptionIfAckOnJobStart() {
        // ackOnJobStart=trueの場合、
        // DefaultJobExecutionListenerで明示的にメッセージをacknowledgeするため、
        // SQSServerConfigクラスにて無駄にacknowledgeさせない実装例としている
        // エラー時のacknowledge漏れがないよう、acknowledge実行している        
        if (sqsServerConfigurationProperties.isAckOnJobStart()) {
            jmsMessageManager.acknowledge();
        }
    }
}