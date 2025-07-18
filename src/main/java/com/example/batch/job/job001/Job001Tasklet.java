package com.example.batch.job.job001;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.domain.sharedservice.TodoSharedService;
import com.example.batch.job.common.record.TodoRecord;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Taskletのサンプル実装。 TodoListのCSVファイルを読み込み、一括でBackendアプリケーションへTodoの登録依頼を実施する。
 *
 */
@StepScope
@Component
@Slf4j
@RequiredArgsConstructor
public class Job001Tasklet implements Tasklet {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final FlatFileItemReader<TodoRecord> todoListFileReader;
    // 単項目チェック用のValidator
    private final Validator<TodoRecord> validator;
    // 相関項目チェック用のValidator
    private final Validator<TodoRecord> todoRecordSpringValidator;

    private final TodoSharedService todoSharedService;

    @Value("${input.file.name:files/input/todolist.csv}")
    private String defaultInputFileName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        appLogger.debug("Job001Tasklet実行");

        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        String param01 = stepExecution.getJobParameters().getString("param01");
        String param02 = stepExecution.getJobParameters().getString("param02");
        String inputFileName = stepExecution.getJobParameters().getString("input-file-name", defaultInputFileName);
        appLogger.debug("param01:{},param02:{},inputFileName:{}", param01, param02, inputFileName);

        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        jobExecutionContext.put("input.file.name", inputFileName);
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        TodoRecord item;
        try {
            todoListFileReader.open(executionContext);
            while ((item = todoListFileReader.read()) != null) {
                log.debug(item.toString());
                // 入力チェック
                try {
                    // 単項目チェック
                    validator.validate(item);
                    // 相関項目チェック
                    todoRecordSpringValidator.validate(item);
                } catch (ValidationException e) {
                    // 入力チェックエラーの場合は、レコードの何行目でエラーが発生したかをログを出しリスロー
                    appLogger.warn(MessageIds.W_EX_2001, e, inputFileName, item.getCount());
                    throw e;
                }
                // ビジネスロジックの実行
                todoSharedService.registerTodo(item.getTodoTitle());
            }
        } finally {
            todoListFileReader.close();
        }
        return RepeatStatus.FINISHED;
    }
}
