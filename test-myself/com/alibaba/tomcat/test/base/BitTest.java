package com.alibaba.tomcat.test.base;

public class BitTest {
	public static void easyBit(){
		int a = 1 << 0;
		int b = 1 << 1;
		int c = 1 << 2;
		int d = 1 << 3;
		int e = 1 << 4;		
		System.out.println(a + "," + b + "," + c + "," + d + "," + e);
		System.out.println((15 & d) == d);
		System.out.println((12 & b) == b);
		System.out.println((19 & e) == e);
		
		System.out.println(19 & ~e);		// 取反再与 等于说减去了这个值 牛逼
	}
	
	public static void main(String[] args) {
		easyBit();
	}
}
