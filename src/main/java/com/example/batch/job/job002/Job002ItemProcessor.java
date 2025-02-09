package com.example.batch.job.job002;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import com.example.batch.domain.model.Todo;
import com.example.batch.domain.record.TodoRecord;
import com.example.batch.domain.repository.TodoRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class Job002ItemProcessor implements ItemProcessor<TodoRecord, TodoRecord> {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final TodoRepository todoRepository;

    @Override
    public TodoRecord process(TodoRecord item) throws Exception {
        appLogger.debug("Job002ItemProcessor実行:{}", item.getTodoTitle());
        Todo todo = Todo.builder().todoTitle(item.getTodoTitle()).build();
        todoRepository.create(todo);
        return item;
    }

}
