package com.alibaba.tomcat.test.nio;
   
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel; 
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator; 

public class Test6 {
	private static byte end = '\n'; 
	
	public static void main(String[] args) throws Exception {
		System.out.println("start server.");
		ServerSocketChannel channel = null;  
        try {  
            channel = ServerSocketChannel.open();  
            SocketAddress address = new InetSocketAddress(8088);  
            channel.configureBlocking(false);  
            ServerSocket socket = channel.socket();  
            socket.setReuseAddress(true);  
            socket.bind(address);  
            Selector selector = Selector.open();  
            // 注册ServerSocketChannel到selector
            channel.register(selector, SelectionKey.OP_ACCEPT);  
            while (selector.isOpen()) {  
                if (selector.select(1000) == 0) {  
                    System.out.print("...");  
                    continue;  
                }  
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
                while (it.hasNext()) {  
                    SelectionKey key = it.next();   
                    if (key.isAcceptable()) {  
                        accept(key);  
                    } else if (key.isReadable()) {  
                        read(key);  
                    } else if (key.isWritable()) {  
                        write(key);  
                    }  
                    it.remove();  
                }  
                  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            if(channel != null){  
                try{  
                    channel.close();  
                }catch(Exception ex){  
                    ex.printStackTrace();  
                }  
            }  
        }  
	}
	
	 private static void accept(SelectionKey key) throws Exception {  
         SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();  
         socketChannel.configureBlocking(false);  
         // 又把socketChannel绑到这个selector
         socketChannel.register(key.selector(), SelectionKey.OP_READ);  
         // 获取连接后,开始准备接受client的write,即当前socket的read.  
         System.out.println("Server accept...");  
     }  

     private static void read(SelectionKey key) throws Exception {  
         System.out.println("Server reads begin..");  
         SocketChannel socketChannel = (SocketChannel) key.channel();  
         ByteBuffer buffer = ByteBuffer.allocate(1024);  
         while (socketChannel.read(buffer) != -1) {  
             byte last = buffer.get(buffer.position()-1);  
             System.out.println("l:" + buffer.position() + "//" + last + "//" + end);  
             if(last == end){  
                 break;  
             }  
             Thread.sleep(500);  
         }  
         buffer.flip();  
         Charset charset = Charset.defaultCharset();  
         CharBuffer charBuffer = charset.decode(buffer);  
         System.out.println("Server read:" + charBuffer.toString());  
         key.interestOps(SelectionKey.OP_WRITE);  
     }  

     private static void write(SelectionKey key) throws Exception {  
         SocketChannel socketChannel = (SocketChannel) key.channel();  
         Charset charset = Charset.defaultCharset();  
         String data = "S : " + System.currentTimeMillis();  
         ByteBuffer byteBuffer = charset.encode(data);  
         int limit = byteBuffer.limit();  
         byteBuffer.clear();  
         byteBuffer.position(limit);  
         byteBuffer.put(end);  
         byteBuffer.flip();  
         while(byteBuffer.hasRemaining()){  
             socketChannel.write(byteBuffer);  
         }  
         key.interestOps(SelectionKey.OP_READ);  
     }  
}
