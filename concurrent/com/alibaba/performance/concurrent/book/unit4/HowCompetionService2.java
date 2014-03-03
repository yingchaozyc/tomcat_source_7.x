package com.alibaba.performance.concurrent.book.unit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HowCompetionService2 {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		CompletionService<Integer> service = new ExecutorCompletionService<Integer>(executor);
		
		List<Future<Integer>> futureList = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 7; i++) {
			futureList.add(service.submit(new CompetionCallable()));
		}
		
		Future<Integer> futureResult = service.poll(); 
		
		System.out.println(futureResult.get());	 
	}
}

class CompetionCallable implements Callable<Integer>{

	@Override
	public Integer call() throws Exception {
		int second = (int)(Math.random() * 10);
		
		try {
			TimeUnit.SECONDS.sleep(second);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} 
		
		System.out.println(Thread.currentThread().getName() + "执行完毕，计算结果是:" + second);
		return second;
	}
	
}
