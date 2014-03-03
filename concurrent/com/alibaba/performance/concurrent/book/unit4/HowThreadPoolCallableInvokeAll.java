package com.alibaba.performance.concurrent.book.unit4; 

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit; 

public class HowThreadPoolCallableInvokeAll {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		List<Callable<Integer>> taskList = new ArrayList<Callable<Integer>>();
		for (int i = 0; i < 5; i++) { 
			taskList.add(new InovkeAllTask("thread-" + i));
		}
		 
		try {
			List<Future<Integer>> futureList = executor.invokeAll(taskList);
			
			System.out.println("所有callable都跑完了。");
			
			for (Future<Integer> future : futureList) {
				int time = future.get();
				System.out.println(time);
			}
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} catch (ExecutionException e) { 
			e.printStackTrace();
		} 
	}
}

class InovkeAllTask implements Callable<Integer>{

	private String taskName;
	
	public InovkeAllTask(String taskName) { 
		this.taskName = taskName;
	}

	@Override
	public Integer call() throws Exception {
		long duration=(long)(Math.random()*10);  
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(taskName + "执行完毕，等待其他线程...");
		return (int)duration;
	}
 
}
