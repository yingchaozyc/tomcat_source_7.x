package com.alibaba.tomcat.test.concurrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketChannelTest {
	public static void startServer(){
		try {
			ServerSocketChannel socket = ServerSocketChannel.open();
			socket.bind(new InetSocketAddress(8848)); 
			socket.configureBlocking(false);
		    SocketChannel channel = socket.accept(); 
		    channel.
		} catch (IOException e) { 
			e.printStackTrace();
		}
 
	}
}
