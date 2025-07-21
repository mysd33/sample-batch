package com.example.fw.batch.async.store;

import jakarta.jms.Message;

/**
 * 
 * JMSメッセージを管理するインタフェース
 *
 */
public interface JmsMessageManager {
    /**
     * メッセージを管理下におく
     * 
     * @param message メッセージ
     */
    void manage(Message message);

    /**
     * メッセージをキューへACK（削除）する
     */
    void acknowledge();
}
