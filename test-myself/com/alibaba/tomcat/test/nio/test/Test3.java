package com.alibaba.tomcat.test.nio.test;

import java.io.File; 
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Test3 {
	public static void main(String[] args) throws Exception {
		RandomAccessFile from = new RandomAccessFile(
				new File("D:/Paginator.java"), "rw");
		
		FileChannel channel = from.getChannel();
		
		ByteBuffer buffer = ByteBuffer.allocate(102400);
		channel.read(buffer);
		
		buffer.flip();
		StringBuffer str = new StringBuffer();
		while(buffer.hasRemaining()){
			str.append((char)buffer.get());
		}
		
		System.out.println(str);
	}
}
