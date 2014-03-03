package com.alibaba.performance.concurrent.book.unit1;

public class HowInterrupt2 {
	public static void main(String[] args) {
		Thread t1 = new Thread(new Worker());
		t1.start();
		
		System.out.println(t1.isInterrupted());
		t1.interrupt(); 
		System.out.println(t1.isInterrupted());
	}
}

class Worker implements Runnable{

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("now:" + Thread.currentThread().isInterrupted());
			System.out.println("卧槽有人想干掉我！");
			System.out.println(e);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) { 
				e1.printStackTrace();
			}
		}	
	}
	
}