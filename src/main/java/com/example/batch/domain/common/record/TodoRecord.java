package com.example.batch.domain.common.record;

import java.io.Serializable;

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
public class TodoRecord implements Serializable {
	private static final long serialVersionUID = -8221174350955399012L;
	//タイトル
	private String todoTitle;
}