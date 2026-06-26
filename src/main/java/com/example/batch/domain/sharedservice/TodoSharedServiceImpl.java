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
    public void registerTodo(Todo todo) {
        todoRepository.create(todo);
    }

}
