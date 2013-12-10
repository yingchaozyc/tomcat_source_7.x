package com.alibaba.tomcat.test.nio;

import java.io.File; 
import java.io.RandomAccessFile; 
import java.nio.channels.FileChannel;

public class Test1 {
	public static void main(String[] args) throws Exception {
		RandomAccessFile from = new RandomAccessFile(
				new File("D:/paginator.vm"), "rw");
		
		RandomAccessFile to = new RandomAccessFile(
				new File("E:/programme/opensourcecode/nio/to.txt"), "rw");
		
		FileChannel fromChannel = from.getChannel();
		FileChannel toChannel = to.getChannel();
		
		toChannel.transferFrom(fromChannel, 0, fromChannel.size()); 
		
		fromChannel.close();
		toChannel.close();
	}
}
