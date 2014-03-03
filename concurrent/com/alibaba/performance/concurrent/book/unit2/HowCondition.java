package com.alibaba.performance.concurrent.book.unit2;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HowCondition {
	public static void main(String[] args) {
		FileMock mock = new FileMock(100, 10);
		Buffer buffer = new Buffer(1);
		
		Producer proceduer = new Producer(mock, buffer); 
		Thread t1 = new Thread(proceduer, "PROCEDUER"); 
		t1.start();
		
		Consumers consumer = new Consumers(buffer);
		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(consumer, "CONSUMER-" + i);
			t.start();
		}
	}
}

class FileMock{
	private String content[];
	private int index;
	
	public FileMock(int size, int length){
		content = new String[size];
		
		// 将content初始化为大小为size的数组，其中每个元素都是长度为length(其实不一定为length，至少大于length)的字符串
		for (int i = 0; i < size; i++) {
			StringBuilder buffer = new StringBuilder(length);
			for (int j = 0; j < length; j++) {
				int indice = (int)(Math.random() * 255);
				buffer.append(indice);
			}
			
			content[i] = buffer.toString();
		}
		
		// 下标初始化为0
		index = 0;
	}
	
	public boolean hasMoreLines(){
		return index < content.length;
	}
	
	public String getLine(){
		if(this.hasMoreLines()){ 
			return content[index++];
		}
		return null;
	}
}

/**
 * 线程共享对象
 * 
 * @author yingchao.zyc
 *
 * @date 2014-2-14 下午1:51:40
 *
 */
class Buffer{
	// 存储共享数据
	LinkedList<String> buffer;
	
	// 存储缓冲区长度
	int maxSize;
	
	// 控制修改缓冲区代码块的访问 
	ReentrantLock lock;
	 
	Condition lines;
	
	Condition space;
	
	// 缓冲区中是否有行
	boolean pendingLines;
	
	public Buffer(int maxSize){
		this.maxSize = maxSize;
		buffer = new LinkedList<String>();
		lock = new ReentrantLock();
		
		lines = lock.newCondition();
		space = lock.newCondition();
		
		pendingLines = true;
	}
	
	public void setPendingLines(boolean pendingLines){
		this.pendingLines = pendingLines;
	}
	
	public boolean hasPendingLines(){
		return pendingLines || buffer.size() > 0;
	}
	
	public void insert(String line){
		lock.lock();
		try {
			// 如果满了，条件等待
			while(buffer.size() == maxSize){
				System.out.println("啊。。缓冲区怎么这么满。等待 ........");
				space.await();
			}
			
			buffer.offer(line);
			
			System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread().getName(),buffer.size());

			// lines条件唤醒
			lines.signalAll();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			lock.unlock();
		}
	}
	
	public String get(){
		String line = null;
		lock.lock();
		try {
			// 如果缓冲为空  条件等待
			while((buffer.size() == 0)){
				System.out.println("啊。。缓冲区怎么是空的。等待 ........我是" + Thread.currentThread().getName());
				lines.await();
			}
			
			if(hasPendingLines()){
				line = buffer.poll();
				
				System.out.printf("%s: Line Readed: %d\n",Thread.currentThread().getName(),buffer.size());

				// 已经拿走一个 缓冲区满条件解除
				space.signalAll();
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			lock.unlock();
		}
		
		return line;
	}
}

class Producer implements Runnable{

	private FileMock mock;
	
	private Buffer buffer;
	
	public Producer(FileMock mock, Buffer buffer){
		this.mock = mock;
		this.buffer = buffer;
	} 
	
	@Override
	public void run() {
		buffer.setPendingLines(true);
		while(mock.hasMoreLines()){
			String line = mock.getLine();
			buffer.insert(line);
		}
		
		buffer.setPendingLines(false);
	}
	
}

class Consumers implements Runnable{

	private Buffer buffer;
	
	public Consumers(Buffer buffer){
		this.buffer = buffer;
	}
	
	@Override
	public void run() {
		while(buffer.hasPendingLines()){
			String line = buffer.get();
			processLine(line);
		}
	}
	
	private void processLine(String line) { 
		try { 
			Random random=new Random(); 
			Thread.sleep(random.nextInt(100)); 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} 
	}

}



















