package com.example.fw.batch.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

@Configuration
public class SpringBatchCommonConfig {
    /**
     * ContextPropagatingTaskDecoratorを使用して、スレッド間でTraceIDの値を伝播させるTaskExecutorを定義します。
     * これにより、非同期タスク実行時にThreadLocalの値が失われる問題を解決できます。
     */
    @Bean
    TaskExecutor contextPropagatingParallelTaskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("job004-thread-");
        taskExecutor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        return taskExecutor;
    }

    //  @formatter:off
    // 
    // Spring Bootのスレッドプールに任せない場合は、下記のTaskExecutor Bean定義を有効化する
    //private final SpringBatchConfigurationProperties springBatchConfigurationProperties;
    /**
     * VirtualThread有効時のPartitioning Step（多重実行）用のTaskExecutor
     * 
     */
    /*
    @Bean
    @ConditionalOnProperty(prefix = "spring.threads.virtual", name = "enabled", havingValue = "true", matchIfMissing = false)
    TaskExecutor parallelVirtualThreadTaskExecutor() {
        return new VirtualThreadTaskExecutor(springBatchConfigurationProperties.getThreadNamePrefix());
    }*/

    /**
     * VirtualThread無効時のPartitioning Step（多重実行）用のTheadPool版TaskExecutor
     * 
     */
    /*
    @Bean
    @ConditionalOnProperty(prefix = "spring.threads.virtual", name = "enabled", havingValue = "false", matchIfMissing = true)
    TaskExecutor parallelThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(springBatchConfigurationProperties.getThreadCorePoolSize());
        executor.setMaxPoolSize(springBatchConfigurationProperties.getThreadMaxPoolSize());
        executor.setQueueCapacity(springBatchConfigurationProperties.getQueueCapacity());
        return executor;
    }*/
    //@formatter:on
}
