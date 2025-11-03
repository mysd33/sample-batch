package com.example.batch.job.job004;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.batch.domain.model.User;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@StepScope
@Component
@Slf4j
public class Job004ItemProcessor implements ItemProcessor<User, User> {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

    @Override
    public User process(User item) throws Exception {
        // 年齢計算してログ出力
        int age = item.getAge();
        appLogger.debug("{}さんの年齢は{}歳です。", item.getUserName(), age);

        // 現状、そのまま返すだけのサンプル実装
        return item;
    }

}
