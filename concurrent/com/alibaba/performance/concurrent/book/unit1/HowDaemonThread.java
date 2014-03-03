package com.alibaba.performance.concurrent.book.unit1;

public class HowDaemonThread {
	public static void main(String[] args) {
		Thread t = new Thread(new Daemon());
		t.setDaemon(true);
		t.start();
		
		try {
			Thread.sleep(11);
		} catch (InterruptedException e) { 
			//
		}
	}
}

class Daemon implements Runnable{

	@Override
	public void run() {
		for (int i = 0; i < Long.MAX_VALUE; i++) {
			System.out.println(i);
		}
	}
	
}
