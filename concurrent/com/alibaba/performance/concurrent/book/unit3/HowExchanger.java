package com.alibaba.performance.concurrent.book.unit3;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class HowExchanger {
	public static void main(String[] args) {
		@SuppressWarnings("rawtypes")
		Exchanger exchanger = new Exchanger();
		
		ExchangeRunnable runnable1 = new ExchangeRunnable(exchanger, "a");
		
		ExchangeRunnable runnable2 = new ExchangeRunnable(exchanger, "b");
		
		ExchangeRunnable runnable3 = new ExchangeRunnable(exchanger, "c");
		
		new Thread(runnable1).start();
		
		new Thread(runnable2).start();
		
		new Thread(runnable3).start();
	}
}

class ExchangeRunnable implements Runnable {

	@SuppressWarnings("rawtypes")
	Exchanger exchanger = null;
	
	Object object = null;
	
	@SuppressWarnings("rawtypes")
	public ExchangeRunnable(Exchanger exchanger, Object object){
		this.exchanger = exchanger;
		this.object = object;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			Object previous = this.object;
			
			int second = (int)(Math.random() * 10);
			
			TimeUnit.SECONDS.sleep(second);
			System.out.println(Thread.currentThread().getName() + "我准备好了");
			this.object = this.exchanger.exchange(this.object);
			
			System.out.println(
                    Thread.currentThread().getName() +
                    " exchanged " + previous + " for " + this.object
            );
		} catch (Exception e) {
			 
		}
	}
	
}
