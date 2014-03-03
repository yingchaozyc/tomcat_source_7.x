package com.alibaba.performance.concurrent.book.unit2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HowLockFire {
	public static void main(String[] args) {
		PrintFireQueue printFireQueue = new PrintFireQueue();
		Thread thread[] = new Thread[10];
		
		for (int i = 0; i < 10; i++) {
			thread[i]=new Thread(new JobFire(printFireQueue), "Thread "+ i); 
			thread[i].start();
			System.out.println("thread " + thread[i].getName() + " created.");
			
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		}
	}
}

class JobFire implements Runnable{

	private PrintFireQueue printFireQueue;
	
	public JobFire(PrintFireQueue printFireQueue){
		this.printFireQueue = printFireQueue;
	}
	
	@Override
	public void run() {
		printFireQueue.printJob();   
	}
}

class PrintFireQueue{
	private final Lock queueLock = new ReentrantLock(false);
	
	// 加锁，让线程有秩序的进入
	public void printJob(){ 
		queueLock.lock(); 
		try {  
			System.out.println("Thread " + Thread.currentThread().getName() + " in.");
		} finally {
			queueLock.unlock(); 
		}  
	}
}