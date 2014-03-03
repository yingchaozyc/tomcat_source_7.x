package com.alibaba.performance.concurrent.book.unit1;

import java.util.Random;

public class HowThreadGroupException {
	public static void main(String[] args) {
		MyThreadGroup group = new MyThreadGroup("sucker");
		
		for (int i = 0; i < 3; i++) {
			Thread t = new Thread(group, new Runner()); 
			t.start();
		}
	}
}

class MyThreadGroup extends ThreadGroup{

	public MyThreadGroup(String name) {
		super(name); 
	}
	
	// 覆盖了父类的异常抓捕方法
	public void uncaughtException(Thread t, Throwable e) {

		System.out.println("The thread " + t.getName() + " has thrown an Exception");

		e.printStackTrace(System.out);

		System.out.printf("Terminating the rest of the Threads\n");

		interrupt(); 
	}
}

class Runner implements Runnable{ 

	@Override
	public void run() { 
		Random random = new Random();
		
		while(true){
			System.out.println("====================");
			double num = random.nextDouble();
			System.out.println(num);
			int a = 1000000000 / ((int)(num * 10000)) ;
			System.out.println(a);
			
			if(Thread.currentThread().isInterrupted()){
				System.out.println("卧槽，有人出异常了，然后我被中断了。我是" + Thread.currentThread().getName());
				return;
			}
		}
	}
	
}