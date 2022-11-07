package com.example.fw.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class NoOpItemWriter<T> implements ItemWriter<T>{
	@Override
	public void write(List<? extends T> items) throws Exception {
		//何もしない		
	}
}
