package com.alibaba.performance.concurrent.book.unit4;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HowScheduleThreadPoolExecutor {
	public static void main(String[] args) {
		ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
		
		System.out.println("schedule pool ready to running...");
		
		for (int i = 0; i < 5; i++) {
			SheduleTask task = new SheduleTask();
			executor.schedule(task, 3, TimeUnit.SECONDS);
		}
	}
}

class SheduleTask implements Runnable{

	@Override
	public void run() {
		 System.out.println("hello world");
	}
	
}