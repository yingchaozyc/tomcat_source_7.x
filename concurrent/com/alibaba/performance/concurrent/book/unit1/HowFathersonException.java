package com.alibaba.performance.concurrent.book.unit1;

/**
 * 父线程无法捕捉子线程的异常，线程的异常必须要自己在内部消化掉，这是线程的默认实现.
 * 
 * @author yingchao.zyc
 *
 * @date 2014-2-11 下午3:20:42
 *
 */
public class HowFathersonException {
	public static void main(String[] args) {
		try {
			new Thread(new SonObject()).start();
			System.out.println("over.");
		} catch (Exception e) {
			System.out.println("fuck");
		}
		
		System.out.println("2");
	}
}

class SonObject implements Runnable{

	@Override
	public void run() {
		int a = 3/0;
		System.out.println(a);
	}
	
}
