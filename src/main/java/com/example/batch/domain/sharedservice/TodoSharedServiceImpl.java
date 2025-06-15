package com.example.batch.domain.sharedservice;

import org.springframework.stereotype.Component;

import com.example.batch.domain.model.Todo;
import com.example.batch.domain.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TodoSharedServiceImpl implements TodoSharedService {
    private final TodoRepository todoRepository;

    @Override
    public void registerTodo(String todoTitle) {
        Todo todo = Todo.builder().todoTitle(todoTitle).build();
        todoRepository.create(todo);
    }

}
