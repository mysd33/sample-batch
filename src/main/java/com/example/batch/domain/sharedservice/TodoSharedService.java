package com.example.batch.domain.sharedservice;

import com.example.batch.domain.model.Todo;

/// Todoサービスの操作を行うための共通処理を提供するインターフェース
public interface TodoSharedService {
    /// Todoの登録を行う
    ///
    /// @param todo 登録するTodo
    void registerTodo(Todo todo);

    // ジョブ共通のTodoに関する操作があれば追加する
}
