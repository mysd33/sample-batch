package com.example.batch.job.job004;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import com.example.batch.domain.repository.UserRepository;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ユーザ数でグリッドサイズに合わせて処理件数分割するPartitioner実装クラス
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Job004Partitioner implements Partitioner {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final UserRepository userRepository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>();
        // ユーザ全件取得
        int count = userRepository.count();
        appLogger.debug("ユーザ件数は{}件です。", count);
        int offset = 0;
        // 分割サイズ計算
        int dataSize = (count / gridSize) + 1;
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putInt("dataSize", dataSize);
            context.putInt("offset", offset);
            offset += dataSize;
            map.put("partition" + i, context);
        }

        return map;
    }
}
