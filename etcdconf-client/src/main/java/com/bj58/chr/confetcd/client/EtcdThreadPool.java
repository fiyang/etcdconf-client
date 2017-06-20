package com.bj58.chr.confetcd.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EtcdThreadPool {
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
			2,
			4, 
			10,
			TimeUnit.SECONDS, 
			new LinkedBlockingQueue<Runnable>(1000),
			new ThreadPoolExecutor.AbortPolicy()
			);
	private EtcdThreadPool(){}
	
	public static ThreadPoolExecutor getThreadPool(){
		return executor;
	}
}
