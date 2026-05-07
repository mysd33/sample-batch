package com.example.fw.batch.core.exception;

import com.example.fw.common.exception.BusinessException;
import com.example.fw.common.exception.SystemException;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.logging.MonitoringLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

/// バッチ例外ハンドリング実装クラス
@Slf4j
@Builder
@RequiredArgsConstructor
public class DefaultExceptionHandler implements ExceptionHandler {

    private static final MonitoringLogger monitoringLogger = LoggerFactory.getMonitoringLogger(log);
    private final MessageSource messageSource;
    // 入力エラーのメッセージID
    private final String inputErrorMessageId;
    // システムエラーのメッセージID
    private final String systemErrorMessageId;

    @Override
    public void handle(final JobExecution jobExecution) {
        // 例外の有無チェック
        List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
        if (exceptions.isEmpty()) {
            return;
        }
        for (Throwable ex : exceptions) {
            doHandle(ex);
        }
    }

    private void doHandle(Throwable ex) {
        switch (ex) {
            case ValidationException _ -> {
                // 入力エラーによるシステムエラー
                var error = (BindException) ex.getCause();
                List<FieldError> fieldErrors = error.getFieldErrors();
                var messages = new ArrayList<String>();
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
                monitoringLogger.error(inputErrorMessageId, error, messages.toString());
            }
            case SystemException error ->
                // システム例外でのシステムエラー
                monitoringLogger.error(error.getCode(), error, (Object[]) error.getArgs());
            case BusinessException error ->
                // バッチの場合にビジネス例外を使うことはないが、システムエラー扱い
                monitoringLogger.error(error.getCode(), error, (Object[]) error.getArgs());
            case null, default ->
                // 予期せぬ例外によるシステムエラー
                monitoringLogger.error(systemErrorMessageId, ex);
        }
    }

}
