package com.example.batch.job.common.record;

import org.springframework.batch.infrastructure.item.ItemCountAware;
import com.example.fw.common.validation.CharSet;
import com.example.fw.common.validation.RangeLength;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/// CSVファイルとマッピングするTodoRecordクラス
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRecord implements ItemCountAware {
    private int count;
    // ユーザID
    @NotBlank
    @Email
    private String userId;

    // タイトル
    @NotBlank
    @RangeLength(min = 1, max = 30)
    @CharSet
    private String todoTitle;

    @Override
    public void setItemCount(int count) {
        this.count = count;
    }
}
