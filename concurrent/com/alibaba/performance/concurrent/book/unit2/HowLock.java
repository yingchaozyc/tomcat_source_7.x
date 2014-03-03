package com.alibaba.performance.concurrent.book.unit2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HowLock {
	public static void main(String[] args) {
		PrintQueue printQueue = new PrintQueue();
		Thread thread[] = new Thread[10];
		
		for (int i = 0; i < 10; i++) {
			thread[i]=new Thread(new Job(printQueue), "Thread "+ i);
			thread[i].start();
		}
	}
}

class Job implements Runnable{

	private PrintQueue printQueue;
	
	public Job(PrintQueue printQueue){
		this.printQueue = printQueue;
	}
	
	@Override
	public void run() {
		System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
		printQueue.printJob(new Object());
		System.out.printf("%s: The document has been printed\n",
		Thread.currentThread().getName());
	}
}

class PrintQueue{
	private final Lock queueLock = new ReentrantLock();
	
	// 加锁，让线程有秩序的进入
	public void printJob(Object document){
		System.out.println("fuck");
		//queueLock.lock();
		
		if(!queueLock.tryLock()){
			System.out.println(Thread.currentThread().getName() + " " + "没有拿到锁。");
			return;
		}
		
		try{
			Long duration=(long)(Math.random()*10000); 
			System.out.println(Thread.currentThread().getName() + 
						": PrintQueue: Printing a Job during "+(duration/1000)+ 
						" seconds"); 
			Thread.sleep(duration); 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} finally {
			//queueLock.unlock();
		}
	}
}