package com.example.fw.batch.exception;

import org.springframework.batch.core.JobExecution;

/**
 * 
 * 例外ハンドリング用インタフェース
 *
 */
public interface ExceptionHandler {
	/**
	 * 例外ハンドリング
	 * @param jobExecution　現在のJobExecution
	 */
	void handle(final JobExecution jobExecution);
}
