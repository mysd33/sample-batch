package com.example.fw.batch.jobflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.example.fw.common.constants.FrameworkConstants;

import lombok.Data;

/// ジョブフローのプロパティクラス
@Data
@ConfigurationProperties(prefix = JobflowConfigurationProperties.PROPERTY_PREFIX)
public class JobflowConfigurationProperties {
    // ジョブフローのプロパティプレフィックス
    public static final String PROPERTY_PREFIX = FrameworkConstants.PROPERTY_BASE_NAME + "jobflow";
    // リージョン（デフォルト: ap-northeast-1）
    private String region = "ap-northeast-1";

    // StepFunctionsのタスクトークンをOS環境変数から取得する場合の環境変数名
    private String taskTokenEnvName = "TASK_TOKEN";
}
