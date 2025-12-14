package com.example.fw.batch.async.store;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JmsMessageManagerのデフォルト実装クラス
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultJmsMessageManager implements JmsMessageManager {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final JmsMessageStore jmsMessageStore;

    @Override
    public void manage(Message message) {
        jmsMessageStore.set(message);
    }

    @Override
    public void acknowledge() {
        // ジョブ管理テーブルには登録済みなので、メッセージをACKし、キューからメッセージ削除
        Message message = jmsMessageStore.get();
        if (message != null) {
            String messageId = "";
            try {
                messageId = message.getJMSMessageID();
                appLogger.debug("メッセージをACK:{}", messageId);
                message.acknowledge();
            } catch (JMSException e) {
                // メッセージを削除できなくても例外はスローしない。
                // メッセージ再受信する可能性があるが、AsyncMessageListenerでハンドリングし対処
                appLogger.warn(BatchFrameworkMessageIds.W_FW_ASYNCSV_8005, messageId);
            } finally {
                jmsMessageStore.remove();
            }
        }
    }

}
