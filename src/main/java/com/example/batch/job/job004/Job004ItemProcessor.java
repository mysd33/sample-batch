package com.example.batch.job.job004;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.domain.model.User;
import com.example.batch.domain.model.UserTempInfo;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Partitionig Step、チャックモデルのItemProcessorのサンプル実装
 */
@StepScope
@Component
@Slf4j
public class Job004ItemProcessor implements ItemProcessor<User, UserTempInfo> {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

    @Override
    public UserTempInfo process(User item) throws Exception {
        // 年齢計算してログ出力
        int age = item.getAge();
        appLogger.debug("{}さんの年齢は{}歳です。", item.getUserName(), age);
        // 計算結果をそのまま返すだけのサンプル実装
        UserTempInfo result = UserTempInfo.builder()//
                .userId(item.getUserId())//
                .userName(item.getUserName())//
                .age(age)//
                .build();
        appLogger.info(MessageIds.I_EX_0002, result);
        return result;
    }

}
