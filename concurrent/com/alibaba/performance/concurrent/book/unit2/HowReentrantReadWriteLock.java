package com.alibaba.performance.concurrent.book.unit2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HowReentrantReadWriteLock {
	public static void main(String[] args) {
		PriceInfo priceInfo = new PriceInfo();
		
		new Thread(new Writer(priceInfo)).start();
		for (int i = 0; i < 5; i++) {
			new Thread(new Reader(priceInfo)).start();
		}
	}
}

class Reader implements Runnable{
	
	private PriceInfo priceInfo;
	
	public Reader(PriceInfo priceInfo){
		this.priceInfo = priceInfo;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			priceInfo.getPrice1();
			priceInfo.getPrice2();
			
			try {
				TimeUnit.MILLISECONDS.sleep(800);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		}
	} 
}

class Writer implements Runnable{
	
	private PriceInfo priceInfo;
	
	public Writer(PriceInfo priceInfo){
		this.priceInfo = priceInfo;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			double price1 = Math.random()*10;
			double price2 = Math.random()*10; 
			priceInfo.setPrices(price1, price2);
			
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		}
	} 
}

// 线程共享对象
class PriceInfo{
	private double price1;
	
	private double price2;
	
	private ReadWriteLock lock;
	
	public PriceInfo(){
		price1 = 1.0;
		price2 = 2.0;
		lock = new ReentrantReadWriteLock();
	}
	
	// 用读锁来返回price1
	public double getPrice1(){
		lock.readLock().lock();
		System.out.println("拿到读锁，准备获取price1"); 
		double value = price1; 
		System.out.println("获取到的price1为" + value + ", 准备释放读锁"); 
		lock.readLock().unlock();
		return value;
	}
	
	// 用读锁来返回price2
	public double getPrice2(){
		lock.readLock().lock();
		System.out.println("拿到读锁，准备获取price2"); 
		double value = price2; 
		System.out.println("获取到的price2为" + value + ", 准备释放读锁"); 
		lock.readLock().unlock();
		return value;
	}
	
	// 写锁控制赋值
	public void setPrices(double price1, double price2){ 
		lock.writeLock().lock();
		System.out.println("拿到写锁，准备修改价格。");
		this.price1 = price1;
		this.price2 = price2;
		System.out.println("修改完成。价格被修改成了" + price1 + "," + price2 + ". 写锁即将释放"); 
		lock.writeLock().unlock();
	}
}



