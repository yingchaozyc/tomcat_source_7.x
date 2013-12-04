package com.alibaba.tomcat.test.base;

public class SystemDirTest {
	public static void main(String[] args) {
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);
	}
}
