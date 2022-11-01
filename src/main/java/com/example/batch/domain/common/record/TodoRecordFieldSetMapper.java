package com.example.batch.domain.common.record;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * TodoRecordへマッピングするためのFieldSetMapper実装クラス 
 *
 */
public class TodoRecordFieldSetMapper implements FieldSetMapper<TodoRecord> {
	@Override
	public TodoRecord mapFieldSet(FieldSet fieldSet) throws BindException {
				
		return TodoRecord.builder()
				.todoTitle(fieldSet.readString(0))
				.build();
	}
}
