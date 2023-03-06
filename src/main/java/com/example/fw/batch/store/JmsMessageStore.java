package com.example.fw.batch.store;

import javax.jms.Message;

/**
 * JMSのMessageを保持するストアクラス
 *
 */
public interface JmsMessageStore {
    /**
     * メッセージをストアから取得する
     * 
     * @return メッセージ
     */
    Message get();

    /**
     * メッセージをストアに格納する
     * 
     * @param message メッセージ
     */
    void set(Message message);

    /**
     * メッセージをストアから削除する
     * 
     * @param messageId
     */
    void remove();
}
