package com.example.fw.batch.jobflow.sfn;

/**
 * StepFunctionsのタスクの実行結果を送信するためのインターフェース<br>
 * StepFunctionsでのジョブ間の結果受け渡しに使用する。
 */
public interface SfnTaskResultSender {
    /**
     * タスクの実行成功を送信する
     * 
     * @param jobInstanceId ジョブインスタンスID
     * @param taskToken     タスクトークン
     * @param output        タスクの実行結果
     */
    void sendTaskSuccess(long jobInstanceId, String taskToken, Object output);

    /**
     * タスクの実行成功を送信する<br>
     * タスクの実行結果をJSON文字列で送信する場合に使用する。
     * 
     * @param jobInstanceId ジョブインスタンスID
     * @param taskToken     タスクトークン
     * @param outputJson    タスクの実行結果のJSON文字列
     */
    void sendTaskSuccessByJsonString(long jobInstanceId, String taskToken, String outputJson);

    /**
     * 一度成功しているタスクの実行成功を再送信する<br>
     * 
     * @param jobInstanceId ジョブインスタンスID
     * @param taskToken     タスクトークン
     * @param outputJson    タスクの実行結果のJSON文字列
     */
    void resendTaskSuccessByJsonString(long jobInstanceId, String taskToken, String outputJson);
}
