package com.example.batch.infra.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.RestTemplate;

import com.example.batch.domain.model.Todo;
import com.example.batch.domain.model.TodoList;
import com.example.batch.domain.repository.TodoRepository;
import com.example.batch.infra.httpclient.CircuitBreakerErrorFallback;

import lombok.RequiredArgsConstructor;

/**
 * TodoRepositoryの実装 BackendサービスのREST APIを呼び出す
 */
//WebClient（WebFulx）版のTodoRepository実装を使用しているためコメントアウト
//@Repository
@RequiredArgsConstructor
public class TodoRepositoryImplForBatchByRestTemplate implements TodoRepository {

    private final RestTemplate restTemplate;

    // サーキットブレーカ
    // （参考）https://spring.io/projects/spring-cloud-circuitbreaker
    @SuppressWarnings("rawtypes")
    private final CircuitBreakerFactory cbFactory;

    @Value("${example.api.backend.url}/api/v1/todos/batch")
    private String urlTodosForCreateBatch;

    @Value("${example.api.backend.url}/api/v1/todos")
    private String urlTodos;

    @Value("${example.api.backend.url}/api/v1/todos/{todoId}")
    private String urlTodoById;

    @Override
    public Optional<Todo> findById(String todoId) {
        Todo todo = cbFactory.create("todo_findById").run(
                () -> restTemplate.getForObject(urlTodoById, Todo.class, todoId),
                CircuitBreakerErrorFallback.throwBusinessException());
        return Optional.ofNullable(todo);
    }

    @Override
    public Collection<Todo> findAll() {
        return cbFactory.create("todo_findAll").run(() -> restTemplate.getForObject(urlTodos, TodoList.class),
                // エラーとせずにFallback処理として空のリストを返却する例
                throwable -> new TodoList());
    }

    @Override
    public void create(Todo todo) {
        // バッチ処理のサンプル実行向けに件数チェックされない、create APIのURLを呼び出し
        cbFactory.create("todo_create").run(() -> restTemplate.postForObject(urlTodosForCreateBatch, todo, Todo.class),
                CircuitBreakerErrorFallback.throwBusinessException());
    }

    @Override
    public boolean update(Todo todo) {
        return cbFactory.create("todo_update").run(() -> {
            restTemplate.put(urlTodoById, null, todo.getTodoId());
            return true;
        }, CircuitBreakerErrorFallback.throwBusinessException());
    }

    @Override
    public void delete(Todo todo) {
        cbFactory.create("todo_delete").run(() -> {
            restTemplate.delete(urlTodoById, todo.getTodoId());
            return true;
        }, CircuitBreakerErrorFallback.throwBusinessException());
    }

}