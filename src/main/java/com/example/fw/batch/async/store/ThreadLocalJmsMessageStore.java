package com.example.fw.batch.async.store;

import javax.jms.Message;

/**
 * ThreadLocalを利用したJmsMessageStore実装クラス
 *
 */
public class ThreadLocalJmsMessageStore implements JmsMessageStore {
    private final ThreadLocal<Message> messageStore = new ThreadLocal<>();

    @Override
    public Message get() {
        return messageStore.get();
    }

    @Override
    public void set(Message message) {
        messageStore.set(message);
    }

    @Override
    public void remove() {
        messageStore.remove();
    }

}
