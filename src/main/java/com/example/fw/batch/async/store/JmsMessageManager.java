package com.example.fw.batch.async.store;

import javax.jms.Message;

public interface JmsMessageManager {
	/**
	 * メッセージを管理下におく
	 * @param message メッセージ
	 */
	void manage(Message message);
	
	/**
	 * メッセージをキューへACK（削除）する
	 */
	void acknowledge();
}
