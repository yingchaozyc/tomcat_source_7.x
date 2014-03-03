package com.alibaba.performance.concurrent.book.unit4;
 
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HowNewCachedExecute {
	public static void main(String[] args) {
		Server server = new Server();
		for (int i = 0; i < 100; i++) {
			server.execute(new Request());
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
		System.out.println("done.");
	}
}

class Request implements Runnable{ 
	
	@Override
	public void run() {
		int second = (int)(Math.random() * 10);
		
		try {
			TimeUnit.SECONDS.sleep(second);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} 
		 
	}
	
}

class Server{
	
	private ThreadPoolExecutor executor;
	
	public Server(){
		executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
	}
	
	public void execute(Runnable runnable){
		executor.execute(runnable);
		System.out.println("===> 线程池目前实际线程数:" + executor.getPoolSize() + ", 正在执行任务的线程数:" + executor.getActiveCount()
					  + ", 已经完成的任务数:" + executor.getCompletedTaskCount() );
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