package com.example.batch.job.job004;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.domain.model.User;
import com.example.batch.domain.model.UserTempInfo;
import com.example.batch.domain.rule.UserAgeCalculator;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/// Partitionig Step、チャックモデルのItemProcessorのサンプル実装
@StepScope
@Component
@Slf4j
@RequiredArgsConstructor
public class Job004ItemProcessor implements ItemProcessor<User, UserTempInfo> {

    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final UserAgeCalculator userAgeCalculator;

    @Override
    public UserTempInfo process(User user) throws Exception {
        // 年齢計算してログ出力
        var age = userAgeCalculator.calculate(user.getBirthday());
        appLogger.debug("{}さんの年齢は{}歳です。", user.getUserName(), age);
        // 計算結果をそのまま返すだけのサンプル実装
        UserTempInfo result = UserTempInfo.builder()//
            .userId(user.getUserId())//
            .userName(user.getUserName())//
            .age(age)//
            .build();
        appLogger.info(MessageIds.I_EX_0002, result);
        return result;
    }

}
