package com.alibaba.performance.concurrent.book.unit2;

import java.util.Date;
import java.util.LinkedList; 
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HowWaitNotify {
	public static void main(String[] args) {
		EventStorage storage = new EventStorage();
		
		Thread proceduer = new Thread(new Proceduer(storage));
		proceduer.start();
		
		Thread consumer = new Thread(new Consumer(storage));
		consumer.start();
	}
}

class Proceduer implements Runnable{
	private EventStorage eventStorage;
	
	public Proceduer(EventStorage eventStorage){
		this.eventStorage = eventStorage;
	}

	@Override
	public void run() {
		while(true){
			Random r = new Random();
			int sleepMillSecond = (int)(r.nextDouble() * 100);
			
			try {
				TimeUnit.MILLISECONDS.sleep(sleepMillSecond);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
			
			eventStorage.set();
			// 这里的判断不是很准确，拿到的不是最新的值，(取值前有一个唤醒的操作)
			System.out.println("我是生产者，生产了一个，库里还有" + eventStorage.getStorage().size() + "个。");
		}
	} 
}

class Consumer implements Runnable{
	private EventStorage eventStorage;
	
	public Consumer(EventStorage eventStorage){
		this.eventStorage = eventStorage;
	}
	
	@Override
	public void run() {
		while(true){
			Random r = new Random();
			int sleepMillSecond = (int)(r.nextDouble() * 100);
			
			try {
				TimeUnit.MILLISECONDS.sleep(sleepMillSecond);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
			
			eventStorage.get();
			System.out.println("我是消费者，消费了一个，库里还有" + eventStorage.getStorage().size() + "个。");
		}
	} 
}

class EventStorage{
	private int maxSize;
	
	private LinkedList<Date> storage;
	
	public EventStorage(){
		maxSize = 3;
		storage = new LinkedList<Date>();
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public LinkedList<Date> getStorage() {
		return storage;
	}

	public void setStorage(LinkedList<Date> storage) {
		this.storage = storage;
	}

	public synchronized void set(){
		while(storage.size() == maxSize){
			try {
				System.out.println("o, full, start wait for get...");
				wait();
			} catch (Exception e) {
				System.out.println(e);
			}
		}	
			
		storage.offer(new Date());  
		
		notifyAll(); 
	}
	
	public synchronized void get(){
		while(storage.size() == 0){
			try {
				System.out.println("o, empty, start wait for set...");
				wait();
			} catch (Exception e) {
				System.out.println(e);
			}
		} 

		storage.poll(); 
		
		notifyAll();
	}
}







