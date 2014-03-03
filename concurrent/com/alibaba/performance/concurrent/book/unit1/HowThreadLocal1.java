package com.alibaba.performance.concurrent.book.unit1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HowThreadLocal1 {
	public static void main(String[] args) {
		Killer task = new Killer();

		for (int i = 0; i < 10; i++) { 
			Thread thread = new Thread(task); 
			thread.start();

			try {
				TimeUnit.SECONDS.sleep(2); 
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		}

	}
}

class Killer implements Runnable {

	private Date startDate;

	@Override
	public void run() {
		startDate = new Date();
		System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate);
		
		try {
			TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate);
	}

}
