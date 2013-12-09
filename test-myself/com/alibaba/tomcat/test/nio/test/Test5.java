package com.alibaba.tomcat.test.nio.test;
 
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector; 
import java.nio.channels.SocketChannel;
import java.util.Iterator; 

public class Test5 {
	public static void main(String[] args) throws Exception {
		Selector selector = Selector.open();
		
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(new InetSocketAddress(8082));
		
		channel.register(selector, SelectionKey.OP_READ);
		
		while(true){
			int count = selector.select();
			if(count == 0){
				continue;
			} else{
				System.out.println("client recv!");
			}
			
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
            
            while (it.hasNext()) {  
            	SelectionKey tmpKey = it.next(); 
				if(tmpKey.isReadable()){
					System.out.println("----------------------------------------------client ready to read"); 
					SocketChannel socketChannel = (SocketChannel) tmpKey.channel();
					ByteBuffer buffer = ByteBuffer.allocate(102400);
					
					socketChannel.read(buffer);
					buffer.flip();
					
					StringBuffer str = new StringBuffer();
					while(buffer.hasRemaining()){
						str.append((char)buffer.get());
					}
					
					System.out.println(str);
				
					System.out.println("客户端收到服务器消息:" + str);
				}
				
				it.remove();  
			}
		}
	}
}
