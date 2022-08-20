package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

//JMSListnerの有効化
@EnableJms
@SpringBootApplication
public class SampleBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleBatchApplication.class, args);
	}

}
