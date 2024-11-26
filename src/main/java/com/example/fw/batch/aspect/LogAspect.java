package com.example.fw.batch.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.example.fw.batch.message.BatchFrameworkMessageIds;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.systemdate.SystemDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * バッチ処理向けにRepositoryの性能ログ出力を行うAspect
 *
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class LogAspect {
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

	
	//TODO: 現状、なぜかAOPの処理が通らない
	@Around("@within(org.springframework.stereotype.Repository) || @within(org.apache.ibatis.annotations.Mapper)")
	public Object aroundRepositoryLog(final ProceedingJoinPoint jp) throws Throwable {
		appLogger.trace(BatchFrameworkMessageIds.T_BT_FW_0001, jp.getSignature(), Arrays.asList(jp.getArgs()));
		// 処理時間を計測しログ出力
		long startTime = System.nanoTime();
		try {
			return jp.proceed();
		} finally {
			// 呼び出し処理実行後、処理時間を計測しログ出力
			long endTime = System.nanoTime();
			double elapsedTime = SystemDateUtils.calcElaspedTimeByMilliSecounds(startTime, endTime);
			appLogger.trace(BatchFrameworkMessageIds.T_BT_FW_0002, //
					jp.getSignature(), Arrays.asList(jp.getArgs()), elapsedTime);
		}
	}

}
