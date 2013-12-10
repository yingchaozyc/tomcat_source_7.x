package com.alibaba.tomcat.test.nio;
   
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel; 
import java.nio.channels.SocketChannel; 
import java.util.Iterator; 

public class Test4 {
	public static void main(String[] args) throws Exception {
		System.out.println("start server.");
		Selector selector = Selector.open();
		
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		channel.socket().bind(new InetSocketAddress(8082));
		
		// 注册到selector上
		channel.register(selector, SelectionKey.OP_ACCEPT);	
		 
		while(true){
			int readChannels = selector.select();
			if(readChannels == 0)
				continue;
			
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
            
            while (it.hasNext()) {  
            	SelectionKey tmpKey = it.next(); 
				if(tmpKey.isAcceptable()){
					System.out.println("服务器收到连接了！"); 
					
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel) tmpKey.channel();
					SocketChannel socketChannel = serverSocketChannel.accept();
		            
					
					socketChannel.write(ByteBuffer.wrap("use java nio!".getBytes()));
					socketChannel.configureBlocking(false);
					socketChannel.register(selector, SelectionKey.OP_READ);	
				} 
				
				if(tmpKey.isReadable()){
					SocketChannel socketChannel = (SocketChannel) tmpKey.channel();

					
					ByteBuffer buffer = ByteBuffer.allocate(102400);
					
					socketChannel.read(buffer);
					buffer.flip();
					
					StringBuffer str = new StringBuffer();
					while(buffer.hasRemaining()){
						str.append((char)buffer.get());
					}  
					System.out.println("server get info :" + str);
				}
				
				it.remove();  
			}
		} 
	}
}
