package com.example.batch.domain.job002;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.batch.domain.common.model.Todo;
import com.example.batch.domain.common.record.TodoRecord;
import com.example.batch.domain.common.repository.TodoRepository;

public class Job002ItemProcessor implements ItemProcessor<TodoRecord, TodoRecord>{
	@Autowired
	private TodoRepository todoRepository;
	
	@Override
	public TodoRecord process(TodoRecord item) throws Exception {

		Todo todo = Todo.builder().todoTitle(item.getTodoTitle()).build(); 
		todoRepository.create(todo);	
		return item;
	}

}
