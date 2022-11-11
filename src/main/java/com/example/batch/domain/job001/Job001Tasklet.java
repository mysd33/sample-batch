package com.example.batch.domain.job001;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.batch.domain.common.model.Todo;
import com.example.batch.domain.common.record.TodoRecord;
import com.example.batch.domain.common.repository.TodoRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Taskletのサンプル実装。 TodoListのCSVファイルを読み込み、一括でBackendアプリケーションへTodoの登録依頼を実施する。
 *
 */
@Slf4j
public class Job001Tasklet implements Tasklet {
	private final static ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
	@Qualifier("todoListFileReader")
	@Autowired
	private FlatFileItemReader<TodoRecord> todoListFileReader;

	@Autowired
	private TodoRepository todoRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		appLogger.debug("Job001Tasklet実行");

		StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
		String param01 = stepExecution.getJobParameters().getString("param01");
		String param02 = stepExecution.getJobParameters().getString("param02");
		appLogger.debug("param01:{},param02:{}", param01, param02);

		ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
		jobExecutionContext.put("input.file.name", "files/input/todolist.csv");
		ExecutionContext executionContext = stepExecution.getExecutionContext();

		TodoRecord item;
		try {
			todoListFileReader.open(executionContext);
			while ((item = todoListFileReader.read()) != null) {
				log.debug(item.toString());
				Todo todo = Todo.builder().todoTitle(item.getTodoTitle()).build();
				todoRepository.create(todo);
			}

		} finally {
			todoListFileReader.close();
		}
		return RepeatStatus.FINISHED;
	}
}
