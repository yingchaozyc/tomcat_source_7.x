package com.alibaba.performance.concurrent.book.unit1;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class HowThreadFactory {
	public static void main(String[] args) {
		MyThreadFactory factory = new MyThreadFactory("tribll");
		for (int i = 0; i < 10; i++) {
			factory.newThread(new Rush()).start(); 
		}
	}
}

class Rush implements Runnable{

	@Override
	public void run() { 
		System.out.println("I am Thread " + Thread.currentThread().getName());
	}
	
}


class MyThreadFactory implements ThreadFactory{

	private int counter;

	private String name;

	private List<String> stats;
	
	public MyThreadFactory(String name){
		counter=0;
		this.name=name;
		stats=new ArrayList<String>();
	}
 
	public String getStats(){ 
		StringBuffer buffer=new StringBuffer(); 
		Iterator<String> it=stats.iterator();  
		while (it.hasNext()) { 
			buffer.append(it.next()); 
			buffer.append("\n"); 
		} 
		return buffer.toString(); 
	}
 
	@Override
	public Thread newThread(Runnable r) {
		Thread t=new Thread(r, name + "-Thread_" + counter);
		counter++;
		stats.add(String.format("created thread %d with name %s on %s\n",t.getId(), t.getName(), new Date()));
		return t; 
	} 
}