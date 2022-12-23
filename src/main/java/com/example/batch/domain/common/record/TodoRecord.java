package com.example.batch.domain.common.record;

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
public class TodoRecord {	
	//タイトル
	private String todoTitle;
}
