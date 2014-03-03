package com.alibaba.performance.concurrent.book.unit4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class HowExitCallable {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("start running..");
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		
		Future<Integer> future = executor.submit(new ExitTask()); 
		
		while(!future.isDone()){			
			Thread.sleep(300);
			System.out.println(future.isDone());
		}
		
		System.out.println("done."); 
	}
}	

class ExitTask implements Callable<Integer>{

	@Override
	public Integer call() throws Exception {
		int sleepTime = 3000;
		
		try { 
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		
		return sleepTime;
	}
  
}