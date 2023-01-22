package com.example.fw.batch.exception;

import java.util.List;

import javax.validation.ValidationException;

import org.springframework.batch.core.JobExecution;
import org.springframework.validation.BindException;

import com.example.fw.common.exception.BusinessException;
import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;
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
            if (ex instanceof SystemException error) {                
                monitoringLogger.error(error.getCode(), error, (Object[]) error.getArgs());
            } else if (ex instanceof BusinessException error) {                
                monitoringLogger.error(error.getCode(), error, (Object[]) error.getArgs());
            } else if (ex instanceof ValidationException) {
                BindException error = (BindException) ex.getCause();
                monitoringLogger.error(defaultExceptionMessageId, error, error.getFieldError());
            } else {
                monitoringLogger.error(defaultExceptionMessageId, ex);
            }
        }
    }

}
