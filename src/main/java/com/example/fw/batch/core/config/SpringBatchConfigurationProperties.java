package com.example.fw.batch.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.example.fw.common.constants.FrameworkConstants;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = SpringBatchConfigurationProperties.PROPERTY_PREFIX)
public class SpringBatchConfigurationProperties {
    // Spring Batchのプロパティプレフィックス
    public static final String PROPERTY_PREFIX = FrameworkConstants.PROPERTY_BASE_NAME + "batch";
    // Spring Batchの実行タイプ（async:キューを介した非同期実行、commandline:コマンドライン実行）
    private String type;

    // Partitioning Step（多重実行）用でVirtualThread版TaskExecutorのスレッド名プレフィックス
    private String threadNamePrefix = "prt-virtual-";
    // Partitioning Step（多重実行）用でThreadPool版TaskExecutorのコアプールサイズ
    private int threadCorePoolSize = 5;
    // Partitioning Step（多重実行）用でThreadPool版TaskExecutorの最大プールサイズ
    private int threadMaxPoolSize = 5;
    // Partitioning Step（多重実行）用のThreadPool版TaskExecutorのキューキャパシティサイズ
    private int queueCapacity = 200;

}
