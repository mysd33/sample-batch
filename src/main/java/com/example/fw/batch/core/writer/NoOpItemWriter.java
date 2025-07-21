package com.example.fw.batch.core.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/**
 * 何もしないItemWriter
 *
 */
public class NoOpItemWriter<T> implements ItemWriter<T> {
    @Override
    public void write(Chunk<? extends T> chunk) throws Exception {

        // 何もしない
    }
}
