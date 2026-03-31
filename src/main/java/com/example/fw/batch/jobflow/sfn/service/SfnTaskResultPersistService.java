package com.example.fw.batch.jobflow.sfn.service;

/**
 * Step Functionsのタスク実行結果を永続化するサービスインターフェース
 */
public interface SfnTaskResultPersistService {
    /**
     * タスク実行結果を登録する
     * 
     * @param jobInstanceId ジョブインスタンスID
     * @param taskResult    タスク実行結果
     */
    void createTaskResult(long jobInstanceId, String taskResult);

    /**
     * ジョブインスタンスIDに基づいてタスク実行結果を取得する
     * 
     * @param jobInstanceId ジョブインスタンスID
     * @return タスク実行結果
     */
    String findTaskResultById(long jobInstanceId);
}
