package com.example.batch.infra.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.batch.domain.common.model.Todo;
import com.example.batch.domain.common.model.TodoList;
import com.example.batch.domain.common.repository.TodoRepository;
import com.example.batch.infra.httpclient.CircutiBreakerErrorFallback;
import com.example.batch.infra.httpclient.WebClientResponseErrorHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * TodoRepositoryの実装 BackendサービスのREST APIを呼び出す WebFlux実装
 */
@Repository
@RequiredArgsConstructor
public class TodoRepositoryImplForBatchByWebClient implements TodoRepository {
	private final WebClient webClient;	
	private final WebClientResponseErrorHandler responseErrorHandler;

	//サーキットブレーカ
	//（参考）https://spring.io/projects/spring-cloud-circuitbreaker
	@SuppressWarnings("rawtypes")
	private final ReactiveCircuitBreakerFactory cbFactory;
	
	@Value("${api.backend.url}/api/v1/todos/batch")
	private String urlTodosForCreateBatch;
	
	@Value("${api.backend.url}/api/v1/todos")
	private String urlTodos;

	@Value("${api.backend.url}/api/v1/todos/{todoId}")
	private String urlTodoById;

	// WebClient(WebFlux)版の実装の参考ページ
	// https://news.mynavi.jp/techplus/article/techp5348/
	// https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client
	// https://spring.pleiades.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client
	// https://medium.com/a-developers-odyssey/spring-web-client-exception-handling-cd93cf05b76

	@Override
	public Optional<Todo> findById(String todoId) {
		Mono<Todo> todoMono = webClient.get()
				.uri(urlTodoById, todoId)				
				.retrieve()
				.onStatus(HttpStatus::is4xxClientError,  response -> {
					return  responseErrorHandler.createClientErrorException(response);
				})
				.onStatus(HttpStatus::is5xxServerError,response -> {
					return  responseErrorHandler.createServerErrorException(response);
				}) 
				.bodyToMono(Todo.class)
				.transform(it -> cbFactory.create("todo_findById")
						.run(it, CircutiBreakerErrorFallback.returnMonoBusinessException()));
		return todoMono.blockOptional();
	}

	@Override
	public Collection<Todo> findAll() {
		Mono<TodoList> todoListMono = webClient.get().uri(urlTodos)
				.retrieve()
				.onStatus(HttpStatus::is4xxClientError,  response -> {
					return  responseErrorHandler.createClientErrorException(response);
				})
				.onStatus(HttpStatus::is5xxServerError,response -> {
					return  responseErrorHandler.createServerErrorException(response);
				}) 
				.bodyToMono(TodoList.class)
				// Fallback時にエラーとせずに空のリストを例
				.transform(it -> cbFactory.create("todo_findAll")
						.run(it, throwable -> Mono.just(new TodoList())));
		TodoList list = todoListMono.block();
		return list;
	}

	@Override
	public void create(Todo todo) {
		//バッチ処理のサンプル実行向けに件数チェックされない、create APIのURLを呼び出し
		webClient.post().uri(urlTodosForCreateBatch)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(todo)
				.retrieve()
				.onStatus(HttpStatus::is4xxClientError,  response -> {
					return  responseErrorHandler.createClientErrorException(response);
				})
				.onStatus(HttpStatus::is5xxServerError,response -> {
					return  responseErrorHandler.createServerErrorException(response);
				}) 
				.bodyToMono(Todo.class)
				.transform(it -> cbFactory.create("todo_create").run(it,
						CircutiBreakerErrorFallback.returnMonoBusinessException()))
				.block();
	}

	@Override
	public boolean update(Todo todo) {
		webClient.put().uri(urlTodoById, todo.getTodoId())
				.retrieve()
				.onStatus(HttpStatus::is4xxClientError,  response -> {
					return  responseErrorHandler.createClientErrorException(response);
				})
				.onStatus(HttpStatus::is5xxServerError,response -> {
					return  responseErrorHandler.createServerErrorException(response);
				}) 
				.bodyToMono(Todo.class)
				.transform(it -> cbFactory.create("todo_update").run(it,
						CircutiBreakerErrorFallback.returnMonoBusinessException()))
				.block();
		return true;
	}

	@Override
	public void delete(Todo todo) {
		webClient.delete().uri(urlTodoById, todo.getTodoId())
				.retrieve()
				.onStatus(HttpStatus::is4xxClientError,  response -> {
					return  responseErrorHandler.createClientErrorException(response);
				})
				.onStatus(HttpStatus::is5xxServerError,response -> {
					return  responseErrorHandler.createServerErrorException(response);
				})  
				.bodyToMono(Void.class)
				.transform(it -> cbFactory.create("todo_delete").run(it,
						CircutiBreakerErrorFallback.returnMonoBusinessException()))
				.block();
	}

}