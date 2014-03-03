package com.alibaba.performance.concurrent.book.unit2;

/**
 * 模拟一家电影院有两个屏幕和两个售票处。当一个售票处出售门票,
 * 它们用于两个电影院的其中一个，但不能用于两个，所以在每个电影院的免费席位的数量是独立的属性.
 * 
 * @author yingchao.zyc
 *
 * @date 2014-2-12 下午12:56:31
 *
 */
@SuppressWarnings("unused")
public class HowSynchronizedProperties {

	public static void main(String[] args) {
		
		Thread screen1 = new Thread(new Screen(50));
		Thread screen2 = new Thread(new Screen(80));
		
		Thread window1 = new Thread(new SellerWindow());
		Thread window2 = new Thread(new SellerWindow());
	}
}

// 电影票
class Ticket{
	// 电影票是否有效
	private boolean effect;

	public boolean isEffect() {
		return effect;
	}

	public void setEffect(boolean effect) {
		this.effect = effect;
	} 
}

// 屏幕
class Screen implements Runnable{ 
	// 座位数
	@SuppressWarnings("unused")
	private int seat;
	
	public Screen(int seat){
		this.seat = seat;
	}
	
	@Override
	public void run() {
		System.out.println("I am watching movie...");
	}
	
}

// 售票窗
class SellerWindow implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}