package com.example.batch.domain.sharedservice;

/**
 * Todoサービスの操作を行うための共通処理を提供するインターフェース
 * 
 */
public interface TodoSharedService {
    /**
     * Todoの登録を行う
     *
     * @param todoTitle 登録するTodoのタイトル
     */
    void registerTodo(String todoTitle);

    // ジョブ共通のTodoに関する操作があれば追加する
}
