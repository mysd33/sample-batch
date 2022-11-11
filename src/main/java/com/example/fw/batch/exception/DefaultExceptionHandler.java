package com.example.fw.batch.exception;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.validation.BindException;

import com.example.fw.common.exception.BusinessException;
import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * バッチ例外ハンドリング実装クラス
 *
 */
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler {
	private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);
	
	@Setter
	private String defaultExceptionMessageId;

	@Override
	public void handle(final JobExecution jobExecution) {
		// 例外の有無チェック
		List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
		if (exceptions.isEmpty()) {
			return;
		}
		for (Throwable ex : exceptions) {
			if (ex instanceof SystemException) {
				SystemException error = (SystemException) ex;
				monitoringLogger.error(error.getCode(), error, error.getArgs());
			} else if (ex instanceof BusinessException) {
				BusinessException error = (BusinessException) ex;
				monitoringLogger.error(error.getCode(), error, error.getArgs());
			} else if (ex instanceof ValidationException) {
				BindException error =  (BindException) ex.getCause();
				monitoringLogger.error(defaultExceptionMessageId, error, error.getFieldError());
			} else {
				monitoringLogger.error(defaultExceptionMessageId, ex);
			}
		}
	}

}
