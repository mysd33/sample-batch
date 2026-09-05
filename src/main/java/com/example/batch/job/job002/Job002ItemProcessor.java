package com.example.batch.job.job002;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.domain.sharedservice.TodoSharedService;
import com.example.batch.job.common.mapper.TodoRecordMapper;
import com.example.batch.job.common.record.TodoRecord;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.infrastructure.item.validator.Validator;
import org.springframework.stereotype.Component;

@StepScope
@Component
@Slf4j
@RequiredArgsConstructor
public class Job002ItemProcessor implements ItemProcessor<TodoRecord, TodoRecord> {

    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    // 単項目チェック用のValidator
    private final Validator<TodoRecord> validator;
    // 相関項目チェック用のValidator
    private final Validator<TodoRecord> todoRecordSpringValidator;
    private final TodoRecordMapper todoRecordMapper;
    private final TodoSharedService todoSharedService;

    @Override
    public TodoRecord process(TodoRecord todoRecord) throws Exception {
        appLogger.debug("Job002ItemProcessor実行:{}", todoRecord.getTodoTitle());
        // 入力チェック
        try {
            // 単項目チェック
            validator.validate(todoRecord);
            // 相関項目チェック
            todoRecordSpringValidator.validate(todoRecord);
        } catch (ValidationException e) {
            // 入力チェックエラーの場合は、レコードの何行目でエラーが発生したかをログを出しリスロー
            appLogger.warn(MessageIds.W_EX_2001, "todoファイル", todoRecord.getCount());
            throw e;
        }
        // ビジネスロジックの実行
        todoSharedService.registerTodo(todoRecordMapper.recordToModel(todoRecord));
        return todoRecord;
    }

}
