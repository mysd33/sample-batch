package com.example.batch.domain.repository;

import java.util.Collection;
import java.util.Optional;

import com.example.batch.domain.model.Todo;

/**
 * TodoのRepositoryインタフェース 
 */
public interface TodoRepository {
	/**
	 * Todoを取得する
	 * @param todoId　TodoのID
	 * @return Todo
	 */
    Optional<Todo> findById(String todoId);

    /**
     * Todoを全件取得する。
     * @return Todoの全件リスト
     */
    Collection<Todo> findAll();
    
    /**
     * Todoを作成する
     * @param todo 作成するTodo
     */
    void create(Todo todo);

    /**
     * Todoを更新する
     * @param todo 更新するTodo
     * @return 更新成功したかどうか
     */
    boolean update(Todo todo);

   /**
    * Todoを削除する
    * @param todo 削除するTodo
    */
    void delete(Todo todo);

}
