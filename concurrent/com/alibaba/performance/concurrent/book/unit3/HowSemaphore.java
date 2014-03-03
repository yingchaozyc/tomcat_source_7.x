package com.alibaba.performance.concurrent.book.unit3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class HowSemaphore {
	public static void main(String[] args) {
		PrintQueue queue = new PrintQueue(); 
		for (int i = 0; i < 10; i++) {
			new Thread(new Runner(queue)).start();
		}
	}
}

class PrintQueue{
	private Semaphore semaphore;
	
	public PrintQueue(){
		semaphore = new Semaphore(1);
	}

	public void printJob(){ 
		try {
			semaphore.acquire();
			
			System.out.println(Thread.currentThread().getName() + "已经获得semaphore许可，进入成功。");
			
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} finally {
			semaphore.release();
		}
	}
}

class Runner implements Runnable{

	private PrintQueue printQueue;
	
	public Runner(PrintQueue printQueue){
		this.printQueue = printQueue;
	}
	
	@Override
	public void run() {
		printQueue.printJob();
	}
	
}
