package com.example.batch;

import org.springframework.boot.SpringApplication;

/**
 * 純バッチの場合のmainメソッドの定義<br>
 * 参考: https://spring.io/guides/gs/batch-processing
 */
//@SpringBootApplication
public class SampleBatchApplicationForStandardBatch {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SampleBatchApplicationForStandardBatch.class, args)));
    }
}
