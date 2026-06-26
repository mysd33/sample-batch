package com.example.batch.job.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import com.example.batch.domain.model.Todo;
import com.example.batch.job.common.record.TodoRecord;
import org.mapstruct.Mapping;


/// MapStructを使ったTodoのマッパークラス
@Mapper(componentModel = ComponentModel.SPRING)
public interface TodoRecordMapper {

    /// RecordからModelへ変換する
    @Mapping(target = "todoId", ignore = true)
    @Mapping(target = "finished", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Todo recordToModel(TodoRecord record);
}
