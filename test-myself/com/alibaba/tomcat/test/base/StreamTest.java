package com.alibaba.tomcat.test.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class StreamTest {
	// System.out.println 输出重定向
	public static void redirectSystemStream(){
		PrintStream p = null;
		try {
			p = new PrintStream(new File("d:/abc.txt"));
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}
		System.setOut(p);
		
		System.out.println("fuck you !");
	}
	
	public static void main(String[] args) {
		redirectSystemStream();
	}
}
