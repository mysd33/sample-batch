package com.example.fw.batch.jobflow.converter;

import java.util.Properties;

import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;

/**
 * Step Functionから渡されるJobParametersを変換するJobParametersConverterの実装クラス<br>
 * <p>
 * ジョブパラメータの値がJSON形式で受け取るため、配列形式等があると、 デフォルト（DefaultJobParametersConverter）のカンマ区切りの
 * 「key=value,type,identifying」形式のパラメータを変換できないため、値部分をそのまま文字型のパラメータとして変換する
 * 独自のJobParametersConverterを実装する。
 */
public class SfnJobParametersConverter implements JobParametersConverter {

    @Override
    public @NonNull JobParameters getJobParameters(@NonNull Properties properties) {
        JobParametersBuilder builder = new JobParametersBuilder();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            builder.addString(key, value);
        }
        return builder.toJobParameters();
    }

    @Override
    public @NonNull Properties getProperties(@NonNull JobParameters params) {
        Properties properties = new Properties();
        for (JobParameter<?> param : params.parameters()) {
            properties.setProperty(param.name(), param.value().toString());
        }
        return properties;
    }

}
