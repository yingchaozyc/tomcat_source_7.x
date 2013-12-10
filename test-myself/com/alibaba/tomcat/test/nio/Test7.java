package com.alibaba.tomcat.test.nio;
 
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector; 
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator; 

public class Test7 {  
    private static byte end = '\n';  
    
	public static void main(String[] args) throws Exception {
		SocketChannel channel = null;  
        try {  
            channel = SocketChannel.open();  
            channel.configureBlocking(false);  
            boolean isSuccess = channel.connect(new InetSocketAddress(8088));  
            if(!isSuccess){  
                while (!channel.finishConnect()) {  
                    System.out.println("Connecting...");  
                    Thread.sleep(1000);  
                }  
            }  
            Selector selector = Selector.open();  
            channel.register(selector, SelectionKey.OP_WRITE);  
            while (selector.isOpen()) {  
                if (selector.select(1000) == 0) {  
                    System.out.println("C...");  
                    continue;  
                }  
                Iterator<SelectionKey> it = selector.selectedKeys()  
                        .iterator();  
                while (it.hasNext()) {  
                    SelectionKey key = it.next();  
                    if (!key.isValid()) {  
                        channel.close();  
                        return;  
                    }  
                    if (key.isWritable()) {  
                        write(key);  
                    } else if (key.isReadable()) {  
                        read(key);  
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
	
	private static void write(SelectionKey key) throws Exception {  
        SocketChannel socketChannel = (SocketChannel) key.channel();  
        Charset charset = Charset.defaultCharset();  
        String data = "C : " + System.currentTimeMillis();  
        ByteBuffer byteBuffer = charset.encode(data);  
        int limit = byteBuffer.limit();  
        byteBuffer.clear();  
        byteBuffer.position(limit);  
        byteBuffer.put(end);  
        byteBuffer.flip();  
        while(byteBuffer.hasRemaining()){  
            socketChannel.write(byteBuffer);  
        }  
        System.out.println("Client has writeen!");  
        key.interestOps(SelectionKey.OP_READ);  
    }  

    private static void read(SelectionKey key) throws Exception {  
        SocketChannel socketChannel = (SocketChannel) key.channel();  
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);  
        while(socketChannel.read(byteBuffer) != -1){  
            byte last = byteBuffer.get(byteBuffer.position()-1);  
            if(last == end){  
                break;  
            }  
        }   
        byteBuffer.flip();  
        Charset charset = Charset.defaultCharset();  
        CharBuffer charBuffer = charset.decode(byteBuffer);  
        System.out.println("Client read: " + charBuffer.toString());  
        key.interestOps(SelectionKey.OP_WRITE);  
    }  
}
