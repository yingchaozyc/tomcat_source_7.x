package com.alibaba.tomcat.test.nio;
 
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class PipeTest {
	public static void main(String[] args) throws Exception {
		Pipe pipe = Pipe.open();
		
		Pipe.SinkChannel sinkChannel = pipe.sink();
		
		String word = "how i wander what you are.";
		ByteBuffer buffer = ByteBuffer.allocate(128);
		
		buffer.clear();
		buffer.put(word.getBytes());
		buffer.flip();
		
		sinkChannel.write(buffer);
		
		
		Pipe.SourceChannel sourceChannel = pipe.source();
		ByteBuffer buf = ByteBuffer.allocate(128);
		System.out.println("read : " + sourceChannel.read(buf));
	}
}
