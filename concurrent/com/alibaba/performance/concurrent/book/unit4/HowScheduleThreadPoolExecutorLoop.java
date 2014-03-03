package com.alibaba.performance.concurrent.book.unit4;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HowScheduleThreadPoolExecutorLoop {
	public static void main(String[] args) {
		ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
		
		System.out.println("schedule pool ready to running...");
		 
		LoopTask task = new LoopTask();
		
		// 设置为true代表当线程池要关闭，周期任务还是继续进行
		executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
		// withFixed 是从前一个任务执行完毕再加上2秒算起进行调度
		//executor.scheduleWithFixedDelay(task, 0, 2, TimeUnit.SECONDS);  
		
		// atFixed 是判断前一个任务是否超过2秒，如果超过直接开始调度
		executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);  
		
		executor.shutdown();
	}
}

class LoopTask implements Runnable{
 
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		System.out.println("start execute:" + new Date().toLocaleString());
		
		try { 
			Thread.sleep(3000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		
		//System.out.println(new Date().toLocaleString() + " hello world");
	}
	
}