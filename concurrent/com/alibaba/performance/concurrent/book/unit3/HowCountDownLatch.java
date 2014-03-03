package com.alibaba.performance.concurrent.book.unit3;

import java.util.concurrent.CountDownLatch;

public class HowCountDownLatch {
	
}

class VideoConference implements Runnable{

	private final CountDownLatch controller;
	
	public VideoConference(int number){
		controller = new CountDownLatch(number);
	}
	
	@Override
	public void run() {
		 
	}
	
	public void arrive(String name){
		System.out.println(Thread.currentThread().getName() + " is arrive.");
		controller.countDown();
	}
}









