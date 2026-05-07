package com.example.batch.job.common.validator;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.job.common.record.TodoRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/// バッチのTodoRecord用の入力チェック機能の相関項目チェック用のValidatorクラス
@Component
public class TodoRecordCustomValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return TodoRecord.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        var todoRecord = (TodoRecord) target;

        // 本来は、ここに相関項目チェックのロジックを実装する
        // サンプルのCSVファイルがTodoタイトルしかないため
        // ここではdummyという文字列があると入力エラーとする
        if (todoRecord.getTodoTitle() != null && todoRecord.getTodoTitle().contains("dummy")) {
            errors.rejectValue("todoTitle", MessageIds.W_EX_3001);
        }
    }

}
