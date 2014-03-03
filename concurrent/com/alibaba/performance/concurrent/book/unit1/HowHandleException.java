package com.alibaba.performance.concurrent.book.unit1;

import java.lang.Thread.UncaughtExceptionHandler;

public class HowHandleException {
	public static void main(String[] args) {
		Thread t = new Thread(new Hanbger());
		t.setUncaughtExceptionHandler(new UncheckExceptionHandler());
		t.start();
	}
}

class Hanbger implements Runnable{

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			if(i==12){
				@SuppressWarnings("unused")
				int a = Integer.parseInt("abcde");
			}
			System.out.println(i);
		}
	}
	
}

class UncheckExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println(t.getName() + " " + e);
	} 
}