package com.example.fw.batch.async.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.example.fw.common.constants.FrameworkConstants;

import lombok.Data;

/**
 * 
 * SQSのサーバ側のプロパティクラス
 *
 */
@Data
@ConfigurationProperties(prefix = SQSServerConfigurationProperties.PROPERTY_PREFIX)
public class SQSServerConfigurationProperties {
    // TODO: プロパティ名の見直し
    // 非同期実行制御機能のプロパティのプレフィックス
    static final String PROPERTY_PREFIX = FrameworkConstants.PROPERTY_BASE_NAME + "aws.sqs.listener";
    // リスナーが待ち受けするキュー名のプロパティ名
    public static final String LISTENER_QUEUE_NAME_EXPRESSION = "${" + PROPERTY_PREFIX + ".queue-name}";
    // リスナーが待ち受けするキュー名
    private String queueName;
    // trueの時、SpringBatchのジョブ起動時にメッセージ削除（Acknowledge)
    private boolean ackOnJobStart;
    // DefaultJmsListenerContainerFactoryのconcurrencyプロパティに相当
    // リスナの並列実行数
    private String concurrency;

    // DefaultJmsListenerContainerFactoryのreceiveTimeoutプロパティに相当
    // Spring JMSとAmazon SQS Java Messaging Libraryの組み合わせでのマルチスレッドの実装上
    // Amazon SQS Java Messaging Library側のSQSMessageConsumerPrefetchスレッドが
    // SQSからメッセージ受信処理中に、最大20秒タイムアウト設定固定でロングポーリングしている間に
    // Spring JMSのListenerのスレッド側でメッセ時取得町でWait中にタイムアウトし、
    // タイミングの問題でメッセージ取得・処理されず、メッセージの可視性タイムアウトを0秒に設定しリトライするといった
    // 事象が頻発し、メッセージがDLQへ移動してしまう事象が発生することがある。
    // この事象を防止するために、JMS Listener側のタイムアウト値を20秒（20000ミリ秒）
    // （つまり、SQSMessageConsumerPrefetchスレッドのタイムアウト値）以上に設定すること
    // デフォルトでも25秒（25000ミリ秒）に設定している
    private Long receiveTimeout = 25000L;
}
