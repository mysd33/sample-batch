package com.example.fw.batch.core.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import com.example.fw.common.exception.BusinessException;
import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * バッチ例外ハンドリング実装クラス
 *
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultExceptionHandler implements ExceptionHandler {
    private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);
    private final MessageSource messageSource;
    private final String defaultValdationExceptionMessageId;
    private final String defaultExceptionMessageId;

    @Override
    public void handle(final JobExecution jobExecution) {
        // 例外の有無チェック
        List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
        if (exceptions.isEmpty()) {
            return;
        }
        for (Throwable ex : exceptions) {
            if (ex instanceof SystemException error) {
                // システム例外でのシステムエラー
                monitoringLogger.error(error.getCode(), error, (Object[]) error.getArgs());
            } else if (ex instanceof BusinessException error) {
                // バッチの場合にビジネス例外を使うことはないが、システムエラー扱い
                monitoringLogger.error(error.getCode(), error, (Object[]) error.getArgs());
            } else if (ex instanceof ValidationException) {
                // 入力エラーによるシステムエラー
                BindException error = (BindException) ex.getCause();
                List<FieldError> fieldErrors = error.getFieldErrors();
                List<String> messages = new ArrayList<>();
                for (FieldError fieldError : fieldErrors) {
                    String code = fieldError != null ? fieldError.getCode() : null;
                    if (code == null) {
                        continue;
                    }
                    // エラーコードがある場合は、入力エラーメッセージを取得
                    // なお、バッチの単項目チェックの場合、
                    // メッセージIDが(FQDN).messageのメッセージを自動取得できないことや
                    // {value}のような置換文字列が使えないので、メッセージ定義に注意が必要
                    String message = messageSource.getMessage(code, fieldError.getArguments(),
                            fieldError.getDefaultMessage(), Locale.getDefault());
                    messages.add(message);
                }
                monitoringLogger.error(defaultValdationExceptionMessageId, error, messages.toString());
            } else {
                // 予期せぬ例外によるシステムエラー
                monitoringLogger.error(defaultExceptionMessageId, ex);
            }
        }
    }

}
