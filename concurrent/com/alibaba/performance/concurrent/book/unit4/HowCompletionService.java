package com.alibaba.performance.concurrent.book.unit4;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HowCompletionService {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		CompletionService<String> service = new ExecutorCompletionService<String>(executor);
	
		ReportRequest faceRequest=new ReportRequest("Face", service);
		ReportRequest onlineRequest=new ReportRequest("Online",service);
		Thread faceThread=new Thread(faceRequest);
		Thread onlineThread=new Thread(onlineRequest);

		ReportProcessor processor = new ReportProcessor(service);
		Thread sendThread = new Thread(processor);
		
		System.out.printf("Main: Starting the Threads\n");
		faceThread.start();
		onlineThread.start();
		sendThread.start();
		
		try {
			System.out.printf("Main: Waiting for the report	generators.\n");
			faceThread.join();
			onlineThread.join();
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		System.out.printf("Main: Shutting down the executor.\n");
		executor.shutdown();
		
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		
		processor.setEnd(true);
		System.out.println("Main: Ends");  
	}
}

class ReportProcessor implements Runnable{

	private CompletionService<String> completionService;
	
	private boolean end;
	
	public ReportProcessor(CompletionService<String> completionService){
		this.completionService = completionService;
		end = false;
	}
	
	public void setEnd(boolean end){
		this.end = end;
	}
	
	@Override
	public void run() { 
		while(!end){
			try {
				Future<String> result = completionService.poll(20, TimeUnit.SECONDS);
				if(result != null){
					String report = result.get();
					System.out.printf("ReportReceiver: Report Received:%s\n",report);
				}
			} catch (Exception e) { 
				e.printStackTrace();
			}
			
			System.out.printf("ReportSender: End\n");
		}
	} 
}

class ReportRequest implements Runnable{

	private String name;
	
	private CompletionService<String> service;
	
	public ReportRequest(String name, CompletionService<String> service) {
		this.name = name;
		this.service = service;
	}
 
	@Override
	public void run() { 
		ReportGenerator reportGenerator = new ReportGenerator(name, "report");
		service.submit(reportGenerator);
	} 
}

class ReportGenerator implements Callable<String>{

	private String sender;
	
	private String title;
	
	public ReportGenerator(String sender, String title) {
		this.sender = sender;
		this.title = title;
	}

	@Override
	public String call() throws Exception {
		try {
			Long duration = (long) (Math.random() * 10);
			System.out
					.printf("%s_%s: ReportGenerator: Generating a report during %d seconds\n",
							this.sender, this.title, duration);
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String ret = sender + ": " + title;
		return ret;
	}
	
}
