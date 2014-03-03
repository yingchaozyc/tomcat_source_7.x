package com.alibaba.performance.concurrent.book.unit4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors; 
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class HowCallableDone {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		
		// 对Callable包装一层成FutureTask。在这个task里边实现done()方法。执行完默认会回调这个方法。
		DoneFutureTask task = new DoneFutureTask(new DoneCallable());
		executor.execute(task);
		
		while(task.isDone()){
			System.out.println(task.get());
		}
	}
}

class DoneCallable implements Callable<Integer>{

	@Override
	public Integer call() throws Exception {
		Thread.sleep(1000);
		return 10;
	}
	
}

class DoneFutureTask extends FutureTask<Integer>{
  
	public DoneFutureTask(Callable<Integer> callable) {
		super(callable); 
	} 
 
	@Override
	protected void done() {
		System.out.println("done!!!");
	}
} 