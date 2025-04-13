package com.example.batch.job.job003;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.example.batch.domain.model.Todo;
import com.example.batch.domain.record.TodoRecord;
import com.example.batch.domain.repository.TodoRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.objectstorage.DownloadObject;
import com.example.fw.common.objectstorage.ObjectStorageFileAccessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Taskletのサンプル実装。 TodoListのCSVファイルを読み込み、一括でBackendアプリケーションへTodoの登録依頼を実施する。
 *
 */
@StepScope
@Component
@RequiredArgsConstructor
@Slf4j
public class Job003Tasklet implements Tasklet {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private static final String TEMPDIR_RELATIVE_PATH = "files/input/";

    @Qualifier("todoListFileReader")
    private final FlatFileItemReader<TodoRecord> todoListFileReader;
    private final TodoRepository todoRepository;
    private final ObjectStorageFileAccessor objectStorageFileAccessor;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        appLogger.debug("Job003Tasklet実行");
        // ジョブパラメータより、授受したファイルパスを取得
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        String targetFilePath = stepExecution.getJobParameters().getString("filePath");
        appLogger.debug("targetFilePath:{}", targetFilePath);
        // オブジェクトストレージからファイルパスのファイルをダウンロード
        DownloadObject downloadObject = objectStorageFileAccessor.download(targetFilePath);
        // ファイルをローカルファイルシステムへ一時保存したファイルとしてへコピー
        Path tempFilePath = Path.of(FileUtils.getTempDirectoryPath(), TEMPDIR_RELATIVE_PATH,
                downloadObject.getFileName());
        FileUtils.copyInputStreamToFile(downloadObject.getInputStream(), tempFilePath.toFile());
        // FlatFileReaderに一時保存ファイルのパスを引き渡す
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        jobExecutionContext.put("input.file.name", tempFilePath.toString());
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        TodoRecord item;
        try {
            // ファイルを読み込み、Repositoryを呼び出す（REST APIを呼び出しTodoを作成）
            todoListFileReader.open(executionContext);
            while ((item = todoListFileReader.read()) != null) {
                log.debug(item.toString());
                Todo todo = Todo.builder().todoTitle(item.getTodoTitle()).build();
                todoRepository.create(todo);
            }

        } finally {
            todoListFileReader.close();

            // 一時ファイルを削除
            Files.delete(tempFilePath);
        }

        return RepeatStatus.FINISHED;
    }
}
