package com.alibaba.performance.concurrent.book.unit1;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HowThreadGroup {
	public static void main(String[] args) {
		// 线程组
		ThreadGroup group = new ThreadGroup("searchGroup");
		
		Result result = new Result();
		
		// runnable
		SearchTask task = new SearchTask(result);
		
		for (int i = 0; i < 5; i++) {
			// 加入线程组，并启动
			Thread thread = new Thread(group, task);
			thread.start();
			
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			} 
		}
		
		// 线程组创建完成，线程组基本信息输出
		System.out.println("NUMBER OF THREADS IN GROUP:" + group.activeCount());
		System.out.println("INFORMATION ABOUT THE THREAD:");
		group.list();
		
		int activeCount = group.activeCount();
		Thread[] threads = new Thread[activeCount];
		group.enumerate(threads);
		System.out.println("==========================================");
		for (int j = 0; j < activeCount; j++) {
			System.out.println(threads[j].getName() + " " +  threads[j].getState()	);
		}
		
		// 线程组里边的每一个线程设置了睡眠，会在一定时间后结束。
		// 第一个线程跑完退出循环
		waitFinish(group);
		
		// 有第一个线程跑完了(到调用这个方法的时间差可能不止一个线程跑完了)，中断组内其他线程
		group.interrupt();
	}
	
	public static void waitFinish(ThreadGroup threadGroup){
		while(threadGroup.activeCount() > 4){
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class SearchTask implements Runnable{
	private Result result;
	
	public SearchTask(Result result){
		this.result = result;
	}
	
	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		System.out.printf("-------Thread %s: Start\n", name);
		try {
			doTask();
			result.setName(name);
		} catch (InterruptedException e) {
			System.out.printf("- Thread %s: Interrupted\n", name);
			return;
		}
		System.out.printf("-- Thread %s: End\n", name);
	}

	
	private void doTask() throws InterruptedException {
		Random random = new Random((new Date()).getTime());
		int value = (int) (random.nextDouble() * 10);
		System.out.printf("---- Thread %s: %d\n", Thread.currentThread().getName(),value);
		TimeUnit.SECONDS.sleep(value);
	}
}

class Result{
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 
}
