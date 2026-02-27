package com.example.batch.job.job004;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.domain.repository.UserTempInfoRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 前処理用のTaskletのサンプル実装<br>
 * ジョブが何度も実行可能なよう一時テーブルを全件削除しておく
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Job004PreprocessTasklet implements Tasklet {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final UserTempInfoRepository userTempInfoRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // ユーザ一時テーブルを全件削除する
        userTempInfoRepository.deleteAll();
        appLogger.info(MessageIds.I_EX_0001);
        return RepeatStatus.FINISHED;
    }

}
