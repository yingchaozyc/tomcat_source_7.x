package com.alibaba.performance.concurrent.book.unit2;

public class HowSynchronized {
	public static void main(String[] args) {
		System.out.println("初始时我的钱数：" + 10000);
		Account account = new Account(10000);
		
		Thread t1 = new Thread(new Customer(account));
		Thread t2 = new Thread(new Bank(account));
		
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
			System.out.println("结束时我的钱数：" + account.getMoney());
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		}
	}
}

class Account{
	private int money;
	
	public Account(int money) { 
		this.money = money;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	 
	public synchronized void addMoney(int addMoney){
		// 故意加上的tmp参数就是为了能更明显的看到这种没有同步导致的数据错误！
		int tmp = money;
		
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) { 
		}
		
		tmp += addMoney;
		money = tmp;
	}
	
	public synchronized void subMoney(int subMoney){
		// 故意加上的tmp参数就是为了能更明显的看到这种没有同步导致的数据错误！
		int tmp = money;
		
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) { 
		}
		
		tmp -= subMoney;
		money = tmp;
	}
}

class Customer implements Runnable{

	private Account account;
	
	public Customer(Account account){
		this.account = account;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			account.addMoney(100);
		}
	} 
}

class Bank implements Runnable{

	private Account account;
	
	public Bank(Account account){
		this.account = account;
	}
	
	
	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			account.subMoney(100);
		}
	}
	
}