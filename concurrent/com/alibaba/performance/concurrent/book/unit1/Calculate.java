package com.alibaba.performance.concurrent.book.unit1;

public class Calculate {
	
	private static Thread[] threads = new Thread[10]; 
	
	public static void main(String[] args) {
		for (int i = 1; i < 10; i++) {
			threads[i] = new Thread(new Machine(i));
			 
			if(i % 2 == 0){
				threads[i].setPriority(Thread.MAX_PRIORITY);
			} else {
				threads[i].setPriority(Thread.MIN_PRIORITY);
			}
			
			threads[i].setName("BK-THREAD-" + i);

			System.out.println("thread now state is ->: " + threads[i].getState().name());
		} 
		
		System.out.println("=====================");
		for (Thread thread : threads) {
			if(thread != null){
				thread.start();
			}
		}
		System.out.println("=====================");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { 
		}
		
		for (Thread thread : threads) {
			if(thread != null){
				System.out.println("thread now state is : " + thread.getState().name());
			}
		}
	}
}

class Machine implements Runnable{

	private int num;
	
	public Machine(int num){
		this.num = num;
	}
	
	@Override
	public void run() {
		for (int i = 1; i < 10; i++) {
			System.out.println(Thread.currentThread().getId() + " " +
							   Thread.currentThread().getName() + " " +
							   Thread.currentThread().getState().name() + " " +
							   i + "*" + num + "=" + i * num);			
		}
	}
}

