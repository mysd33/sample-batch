package com.example.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.batch.domain.record.TodoRecord;
import com.example.fw.batch.core.config.SpringBatchConfigPackage;
import com.example.fw.batch.exception.DefaultExceptionHandler;
import com.example.fw.batch.exception.ExceptionHandler;
import com.example.fw.common.message.CommonFrameworkMessageIds;

/**
 * 
 * Job層のSpringBatchの設定クラス
 *
 */
@Configuration
@ComponentScan(basePackageClasses = SpringBatchConfigPackage.class)
public class JobConfig  {

    /**
     * 集約例外ハンドリングクラス
     */
    @Bean
    public ExceptionHandler defaultExceptionHandler() {
        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();
        defaultExceptionHandler.setDefaultExceptionMessageId(CommonFrameworkMessageIds.E_CM_FW_9001);
        return defaultExceptionHandler;
    }

    /**
     * TodoListの読み込みクラス
     * 
     * @param filePathName ファイルパス名
     */
    @StepScope
    @Bean
    public FlatFileItemReader<TodoRecord> todoListFileReader(
            @Value("#{jobExecutionContext['input.file.name']}") String filePathName) {
        return new FlatFileItemReaderBuilder<TodoRecord>().name("todoListReader")
                .resource(new FileSystemResource(filePathName)).delimited().delimiter(",").names("todoTitle")
                .targetType(TodoRecord.class).encoding("UTF-8").build();
    }

}
