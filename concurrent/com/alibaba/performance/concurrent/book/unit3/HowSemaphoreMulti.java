package com.alibaba.performance.concurrent.book.unit3;

import java.util.concurrent.Semaphore;  
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HowSemaphoreMulti {
	public static void main(String[] args) {
		System.out.println("==========================");
		PrintQueue queue = new PrintQueue(); 
		for (int i = 0; i < 10; i++) {
			new Thread(new Runner(queue)).start();
		}
	}
}

class PrintQueueMulti{
	private boolean freePrinters[];
	
	private Lock lockFreePrinters;
	
	private Semaphore semaphore;
	
	public PrintQueueMulti(){
		semaphore = new Semaphore(3);
		freePrinters = new boolean[3];
		
		for (int i = 0; i < 3; i++) {
			freePrinters[i] = true;
		}
		
		lockFreePrinters = new ReentrantLock();
	}

	public void printJob(){ 
		try { 
			semaphore.acquire(); 
			
			System.out.println(Thread.currentThread().getName() + "已经获得semaphore许可，进入成功。");
			
			//TimeUnit.SECONDS.sleep(6);
			
			int assignPrinters = getPrinters();  
			freePrinters[assignPrinters] = true;
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} finally {
			semaphore.release();
		}
	}

	private int getPrinters() {
		int ret = -1;
		try {
			lockFreePrinters.lock();
			
			for (int i = 0; i < freePrinters.length; i++) {
				if(freePrinters[i]){
					ret = i;
					freePrinters[i] = false;
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			lockFreePrinters.unlock();
		}
		
		return ret;
	}
}

class RunnerMulti implements Runnable{

	private PrintQueue printQueue;
	
	public RunnerMulti(PrintQueue printQueue){
		this.printQueue = printQueue;
	}
	
	@Override
	public void run() {
		printQueue.printJob();
	}
	
}