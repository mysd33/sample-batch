package com.example.batch.job.job002;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.batch.domain.model.Todo;
import com.example.batch.domain.record.TodoRecord;
import com.example.batch.domain.repository.TodoRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Job002ItemProcessor implements ItemProcessor<TodoRecord, TodoRecord> {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

	@Autowired
	private TodoRepository todoRepository;

	@Override
	public TodoRecord process(TodoRecord item) throws Exception {
		appLogger.debug("Job002ItemProcessor実行:{}", item.getTodoTitle());
		Todo todo = Todo.builder().todoTitle(item.getTodoTitle()).build();
		todoRepository.create(todo);
		return item;
	}

}
