package com.alibaba.performance.concurrent.book.unit1;

import java.util.concurrent.TimeUnit;

public class HowJoin {
	public static void main(String[] args) {
		Thread liverpool = new Thread(new LiverPool());
		liverpool.start();
	}
}

class LiverPool implements Runnable{
	
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println("I AM THE PLAYER OF LIVERPOOL OF NO." + i);
			
			if(i == 7){
				System.out.println("read to join");
				Thread chelase = new Thread(new Chelsea());
				chelase.start();
				try {
					// LiverPool暂停，等待chelsea执行完毕
					chelase.join(3000);
				} catch (InterruptedException e) {
					System.out.println("草，我被中断了");
				}
			}
		}
	} 
	
}

class Chelsea implements Runnable{
	
	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println("I have Fernardo Torres!!!!!");
		}
	} 
	
}