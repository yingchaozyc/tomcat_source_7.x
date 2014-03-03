package com.alibaba.performance.concurrent.book.unit1;

import java.util.concurrent.TimeUnit;

public class HowInterrupt {
	public static void main(String[] args) {
		Thread t = new Thread(new Task());
		t.start();
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) { 
			// IGNORE
		}
		
		t.interrupt(); 
		 
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) { 
			// IGNORE
		}

		System.out.println(t.getState());
		System.out.println(t.isInterrupted());
	}	
}

class Task implements Runnable{
	
	int num = 0;
	
 	@Override
	public void run() {
		while(true){
			num ++;
			
			if(Thread.currentThread().isInterrupted()){
				System.out.println("啊！我要被中断, 最后的num是=" + num + ", 当前线程是" + Thread.currentThread()); 
				
				//Thread.interrupted();
				
				return;
			} 
		}
	}
	
}
