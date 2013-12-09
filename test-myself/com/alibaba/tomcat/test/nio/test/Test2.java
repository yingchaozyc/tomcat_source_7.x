package com.alibaba.tomcat.test.nio.test;

import java.io.IOException; 
import java.net.InetSocketAddress; 
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Test2 {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.socket().bind(new InetSocketAddress(8888));
		channel.configureBlocking(false);
		
		while(true){
			SocketChannel c = channel.accept();
			if(c != null){
				ByteBuffer buffer = ByteBuffer.allocate(204800);
				
				c.read(buffer);
				
				buffer.flip();
				
				StringBuffer str = new StringBuffer();
				while(buffer.hasRemaining()){
					str.append((char)buffer.get());
				}
				
				System.out.println(str);
			}
		}
	}
}
