package com.example.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.job.common.record.TodoRecord;
import com.example.fw.batch.core.config.SpringBatchConfigPackage;
import com.example.fw.batch.exception.DefaultExceptionHandler;
import com.example.fw.batch.exception.ExceptionHandler;

/**
 * 
 * Job層のSpringBatchの設定クラス
 *
 */
@Configuration
@ComponentScan(basePackageClasses = SpringBatchConfigPackage.class)
public class JobConfig {

    /**
     * 集約例外ハンドリングクラス
     */
    @Bean
    ExceptionHandler defaultExceptionHandler() {
        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();
        // バリデーションエラーのメッセージIDを設定
        defaultExceptionHandler.setDefaultValdationExceptionMessageId(MessageIds.E_EX_9002);
        defaultExceptionHandler.setDefaultExceptionMessageId(MessageIds.E_EX_9001);
        return defaultExceptionHandler;
    }

    /**
     * TodoListの読み込みクラス
     * 
     * @param filePathName ファイルパス名
     */
    @StepScope
    @Bean
    FlatFileItemReader<TodoRecord> todoListFileReader(
            @Value("#{jobExecutionContext['input.file.name']}") String filePathName) {
        return new FlatFileItemReaderBuilder<TodoRecord>().name("todoListReader")
                .resource(new FileSystemResource(filePathName)).delimited().delimiter(",").names("todoTitle")
                .targetType(TodoRecord.class).encoding("UTF-8").build();
    }

    /**
     * バッチの入力チェック機能のValidatorクラス
     * 
     * @param beanValidator Bean ValidationのValidatorクラス
     */
    @Bean
    SpringValidator<?> validator(Validator beanValidator) {
        SpringValidator<?> springValidator = new SpringValidator<>();
        springValidator.setValidator(beanValidator);
        return springValidator;
    }

    /**
     * バッチの入力チェック機能のBeanValidatorクラス
     */
    @Bean
    Validator beanValidator() {
        try (LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean()) {
            localValidatorFactoryBean.afterPropertiesSet();
            return localValidatorFactoryBean;
        }
    }

}
