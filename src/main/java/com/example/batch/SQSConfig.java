package com.example.batch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.batch.async.config.SQSServerConfigPackage;
import com.example.fw.common.async.config.SQSCommonConfigPackage;

@Configuration
@ComponentScan(basePackageClasses = { SQSCommonConfigPackage.class, SQSServerConfigPackage.class })
public class SQSConfig {
}
