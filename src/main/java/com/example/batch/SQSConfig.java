package com.example.batch;

import com.example.fw.batch.async.config.SQSServerConfigPackage;
import com.example.fw.common.async.config.SQSCommonConfigPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/// 非同期処理実行依頼、非同期処理実行制御(SQS)の設定クラス
@Configuration
@ComponentScan(basePackageClasses = {SQSCommonConfigPackage.class, SQSServerConfigPackage.class})
public class SQSConfig {

}
