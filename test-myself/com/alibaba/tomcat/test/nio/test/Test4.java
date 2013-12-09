package com.alibaba.tomcat.test.nio.test;
   
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel; 
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
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
		            Charset charset = Charset.defaultCharset();  
		            String data = "S : " + System.currentTimeMillis();  
		            ByteBuffer byteBuffer = charset.encode(data);  
		            int limit = byteBuffer.limit();  
		            byteBuffer.clear();  
		            byteBuffer.position(limit);  
		            byteBuffer.put((byte)'\n');  
		            byteBuffer.flip();  
		            while(byteBuffer.hasRemaining()){  
		                socketChannel.write(byteBuffer);  
		            }   
					
					//socketChannel.write(ByteBuffer.wrap("服务器回复:我管坯里!".getBytes())); 
				} 
				
				it.remove();  
			}
		} 
	}
}
