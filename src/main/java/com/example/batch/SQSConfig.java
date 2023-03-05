package com.example.batch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import com.example.fw.batch.async.config.SQSServerConfigPackage;
import com.example.fw.common.async.config.SQSCommonConfigPackage;

/**
 * 非同期処理実行依頼、非同期処理実行制御(SQS)の設定クラス 
 *
 */
//JMSListnerの有効化
@EnableJms
@Configuration
@ComponentScan(basePackageClasses = { SQSCommonConfigPackage.class, SQSServerConfigPackage.class })
public class SQSConfig {
}
