package com.alibaba.performance.concurrent.book.unit4;
 
import java.util.HashMap; 
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
 
 
public class HowCallableFuture {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CallableServer server = new CallableServer(); 
		
		Map<Integer, Future<Long>> map = new HashMap<Integer, Future<Long>>();
		
		for (int i = 1; i < 100; i++) { 
			Future<Long> future = server.submit(new CallableRunner(i));
			
			map.put(i, future);
		}
		
		while(server.getActiveCount() != 0){
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			} 
			
			System.out.println("目前线程池还在运行的任务数:" + server.getActiveCount() + ", 已经完成的任务数:" + server.getCompleteTaskCount());
		}
		
		server.endServer();
		System.out.println("done. start analysis.");
		
		for (Map.Entry<Integer, Future<Long>> entry : map.entrySet()) {
			try {
				System.out.println(entry.getKey() + " 的阶乘是 " + entry.getValue().get());
			} catch (InterruptedException e) { 
				e.printStackTrace();
			} catch (ExecutionException e) { 
				e.printStackTrace();
			}
		}
	}
}

class CallableRunner implements Callable<Long>{
	
	private int num;
	
	public CallableRunner(int num){
		this.num = num;
	}
	
	@Override
	public Long call() throws Exception {
		Long jc = 1L;
		for (int i = 1; i < num; i++) {
			jc *= i;
		}
		
		int second = (int)(Math.random() * 10);
		
		try {
			TimeUnit.SECONDS.sleep(second);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} 
		
		return jc;
	}
	
}

class CallableServer{
	
	private ThreadPoolExecutor executor;
	
	public CallableServer(){
		executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
	}
	
	public void execute(Runnable runnable){
		executor.execute(runnable);
		System.out.println("===> 线程池目前实际线程数:" + executor.getPoolSize()
						 + ", 正在执行任务的线程数:" + executor.getActiveCount()
						 + ", 已经完成的任务数:" + executor.getCompletedTaskCount()
						 + ", 线程池最大大小:" + executor.getMaximumPoolSize());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Future submit(Callable callable){
		Future<Long> future = executor.submit(callable);
		System.out.println("===> 线程池目前实际线程数:" + executor.getPoolSize()
						 + ", 正在执行任务的线程数:" + executor.getActiveCount()
						 + ", 已经完成的任务数:" + executor.getCompletedTaskCount()
						 + ", 线程池最大大小:" + executor.getMaximumPoolSize());
		
		return future;
	}
	
	public void endServer(){
		executor.shutdown();
	}
	
	public int getActiveCount(){
		return executor.getActiveCount();
	}
	
	public long getCompleteTaskCount(){
		return executor.getCompletedTaskCount();
	}
}