package com.example.fw.batch.jobflow.config;

import com.example.fw.common.constants.FrameworkConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/// ジョブフローのプロパティクラス
@Data
@ConfigurationProperties(prefix = JobflowConfigurationProperties.PROPERTY_PREFIX)
public class JobflowConfigurationProperties {

    // ジョブフローのプロパティプレフィックス
    public static final String PROPERTY_PREFIX = FrameworkConstants.PROPERTY_BASE_NAME + "sfn";
    // ジョブフローの有効化フラグ（デフォルト: false）
    private boolean enabled = false;
    // リージョン（デフォルト: ap-northeast-1）
    private String region = "ap-northeast-1";
    // HTTPコネクションプールの最大接続数（AWS SDKのデフォルト値50）
    // https://github.com/aws/aws-sdk-java-v2/blob/master/http-client-spi/src/main/java/software/amazon/awssdk/http/SdkHttpConfigurationOption.java#L151
    private int maxConnections = 50;
    // HTTPコネクション確立時のタイムアウト（ミリ秒。AWS SDKのデフォルト値2秒 = 2000ミリ秒）
    // https://github.com/aws/aws-sdk-java-v2/blob/master/http-client-spi/src/main/java/software/amazon/awssdk/http/SdkHttpConfigurationOption.java#L142
    private int connectionTimeout = 2000;
    // StepFunctionsのタスクトークンをOS環境変数から取得する場合の環境変数名
    private String taskTokenEnvName = "TASK_TOKEN";
}
