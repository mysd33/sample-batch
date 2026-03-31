package com.example.fw.batch.jobflow.sfn;

import com.example.fw.batch.jobflow.sfn.service.SfnTaskResultPersistService;
import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SfnTaskResultSenderのスタブ実装クラス<br>
 * タスクの実行結果を送信する代わりに、ログに出力する。
 */
@Slf4j
@RequiredArgsConstructor
public class SfnTaskResultSenderStub implements SfnTaskResultSender {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final ObjectMapper objectMapper;
    private final SfnTaskResultPersistService taskResultPersistService;

    @Override
    public void sendTaskSuccess(long jobInstanceId, String taskToken, Object output) {
        String outputJson;
        try {
            outputJson = objectMapper.writeValueAsString(output);
        } catch (JsonProcessingException e) {
            throw new SystemException(e, BatchFrameworkMessageIds.E_FW_JBFLW_9001, output.toString());
        }
        sendTaskSuccessByJsonString(jobInstanceId, taskToken, outputJson);
    }

    @Override
    public void sendTaskSuccessByJsonString(long jobInstanceId, String taskToken, String outputJson) {
        appLogger.info(BatchFrameworkMessageIds.I_FW_JBFLW_0001, taskToken, outputJson);
        // 処理結果をDBへ永続化しておく
        taskResultPersistService.createTaskResult(jobInstanceId, outputJson);
        // StepFunctionsには送信せず、ログ出力のみで完了とする
    }

    @Override
    public void resendTaskSuccessByJsonString(long jobInstanceId, String taskToken, String outputJson) {
        appLogger.info(BatchFrameworkMessageIds.I_FW_JBFLW_0001, taskToken, outputJson);
        // 再送信なのでDBの永続化は行わない
        // StepFunctionsには送信せず、ログ出力のみで完了とする
    }

}
