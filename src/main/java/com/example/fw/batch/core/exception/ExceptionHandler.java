package com.example.fw.batch.core.exception;

import org.springframework.batch.core.job.JobExecution;

/**
 * 
 * 例外ハンドリング用インタフェース
 *
 */
public interface ExceptionHandler {
    /**
     * 例外ハンドリング
     * 
     * @param jobExecution 現在のJobExecution
     */
    void handle(final JobExecution jobExecution);
}
