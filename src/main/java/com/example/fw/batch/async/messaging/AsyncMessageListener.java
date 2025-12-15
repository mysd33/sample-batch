package com.example.fw.batch.async.messaging;

import java.util.Map;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import com.example.fw.batch.async.config.SQSServerConfigurationProperties;
import com.example.fw.batch.async.store.JmsMessageManager;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.async.model.JobRequest;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;

import jakarta.annotation.PostConstruct;
import jakarta.jms.Message;
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
    private final JobParametersConverter jobParametersConverter;
    private final JobRepository jobRepository;
    private final JmsMessageManager jmsMessageManager;
    private final SQSServerConfigurationProperties sqsServerConfigurationProperties;
    private JobRegistry jobRegistry;
    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // Note:
    // MapJobRegistry()をBean定義するように変えると、JobのBean名がジョブ名と完全一致していないとジョブが取得できない問題があるため
    // ここで初期化生成している。
    @PostConstruct
    public void init() {
        jobRegistry = new MapJobRegistry();
        this.applicationContext.getBeansOfType(Job.class).values().forEach(job -> {
            try {
                jobRegistry.register(job);
            } catch (DuplicateJobException e) {
                throw new BatchConfigurationException(e);
            }
        });
    }

    /**
     * キューからジョブの要求情報を受信
     * 
     * @param request ジョブの要求情報
     * 
     */
    @JmsListener(destination = SQSServerConfigurationProperties.LISTENER_QUEUE_NAME_EXPRESSION)
    public void onMessage(@Headers final Map<String, String> headers, Message message, final JobRequest request) {
        // メッセージが有効な形式かチェック
        if (!request.isValid()) {
            monitoringLogger.error(BatchFrameworkMessageIds.E_FW_ASYNCSV_9001, null, request);
            return;
        }
        // メッセージID取得
        String messageId = headers.get(JmsHeaders.MESSAGE_ID);
        // メッセージをJmsMessageManagerの管理下におく
        jmsMessageManager.manage(message);

        Long jobExecutionId = null;
        if (request.isRestart()) {
            // ジョブ再実行の場合
            Long preJobExecutionId = request.getJobExecutionId();
            try {
                // SpringBatchで前回ジョブ実行IDを用いてジョブ再実行
                appLogger.info(BatchFrameworkMessageIds.I_FW_ASYNCSV_0003, messageId, preJobExecutionId);
                JobExecution preJobExecution = jobRepository.getJobExecution(preJobExecutionId);
                if (preJobExecution == null) {
                    // 指定されたジョブ実行IDが存在しない場合、警告ログを出力して終了
                    appLogger.warn(BatchFrameworkMessageIds.W_FW_ASYNCSV_8004, messageId, preJobExecutionId);
                    acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
                    return;
                }
                // SpringBatchでジョブ再実行
                jobOperator.restart(preJobExecution);
                appLogger.info(BatchFrameworkMessageIds.I_FW_ASYNCSV_0004, messageId, preJobExecutionId,
                        jobExecutionId);
            } catch (JobRestartException e) {
                monitoringLogger.error(BatchFrameworkMessageIds.E_FW_ASYNCSV_9004, e, messageId, preJobExecutionId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
            }
            return;
        }
        // ジョブ初回実行の場合
        String jobId = request.getJobId();
        try {
            appLogger.info(BatchFrameworkMessageIds.I_FW_ASYNCSV_0001, messageId, jobId, request.toParameterString());
            // ジョブパラメータ変換
            JobParameters jobParameters = jobParametersConverter.getJobParameters(request.toParameterProperties());

            if (jobRepository.getJobInstance(jobId, jobParameters) != null) {
                // 同一ジョブID、ジョブパラメータのジョブインスタンスが存在する場合、二重実行防止のため、警告ログを出力して終了
                appLogger.warn(BatchFrameworkMessageIds.W_FW_ASYNCSV_8001, messageId, jobId,
                        request.toParameterString());
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
                return;
            }
            // ジョブの取得
            Job job = jobRegistry.getJob(jobId);
            if (job == null) {
                // 指定されたジョブが存在しない場合、警告ログを出力して終了
                appLogger.warn(BatchFrameworkMessageIds.W_FW_ASYNCSV_8003, messageId, jobId);
                acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
                return;
            }
            // SpringBatchでジョブ実行
            JobExecution jobExecution = jobOperator.start(job, jobParameters);
            appLogger.info(BatchFrameworkMessageIds.I_FW_ASYNCSV_0002, messageId, jobId, jobExecution.getId());
        } catch (JobInstanceAlreadyCompleteException e) {
            // 同一ジョブID、ジョブパラメータのジョブインスタンス完了済の場合、二重実行防止のため、警告ログを出力して終了
            appLogger.warn(BatchFrameworkMessageIds.W_FW_ASYNCSV_8002, e, messageId, jobId,
                    request.toParameterString());
            acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
        } catch (JobExecutionAlreadyRunningException e) {
            // 同一ジョブID、ジョブパラメータのジョブインスタンスが存在する場合、二重実行防止のため、警告ログを出力して終了
            appLogger.warn(BatchFrameworkMessageIds.W_FW_ASYNCSV_8001, e, messageId, jobId,
                    request.toParameterString());
            acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
        } catch (InvalidJobParametersException e) {
            // ジョブパラメータが不正な場合、エラーログを出力して終了
            monitoringLogger.error(BatchFrameworkMessageIds.E_FW_ASYNCSV_9002, e, messageId, jobId,
                    request.toParameterString());
            acknowledgeExplicitlyOnExceptionIfAckOnJobStart();
        } catch (JobRestartException e) {
            // JobRestartExceptionは予期せぬエラーとしてエラーログを出力して終了
            monitoringLogger.error(BatchFrameworkMessageIds.E_FW_ASYNCSV_9003, e, messageId, jobId);
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