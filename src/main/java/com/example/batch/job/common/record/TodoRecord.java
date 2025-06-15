package com.example.batch.job.common.record;

import org.springframework.batch.item.ItemCountAware;

import com.example.fw.common.validation.CharSet;
import com.example.fw.common.validation.RangeLength;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CSVファイルとマッピングするTodoRecordクラス
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRecord implements ItemCountAware {
    private int count;
    
	//タイトル
    @NotNull
    @RangeLength(min = 1, max = 30)
    @CharSet
	private String todoTitle;

    @Override
    public void setItemCount(int count) {
        this.count = count;        
    }
}
