package com.example.batch.job.job004;

import com.example.batch.domain.message.MessageIds;
import com.example.batch.domain.repository.UserRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.stereotype.Component;

/// ユーザ数でグリッドサイズに合わせて処理件数分割するPartitioner実装クラス
@Component
@RequiredArgsConstructor
@Slf4j
public class Job004Partitioner implements Partitioner {

    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final UserRepository userRepository;

    @Override
    public @NonNull Map<String, ExecutionContext> partition(int gridSize) {
        var map = new HashMap<String, ExecutionContext>();
        // ユーザ全件取得
        var count = userRepository.count();
        appLogger.debug("ユーザ件数は{}件です。", count);
        var offset = 0;
        // 分割サイズ計算
        var dataSize = (count / gridSize) + 1;
        appLogger.info(MessageIds.I_EX_0003, count, dataSize, gridSize);
        for (var i = 0; i < gridSize; i++) {
            var context = new ExecutionContext();
            context.putInt("dataSize", dataSize);
            context.putInt("offset", offset);
            offset += dataSize;
            map.put("partition" + i, context);
        }

        return map;
    }
}
