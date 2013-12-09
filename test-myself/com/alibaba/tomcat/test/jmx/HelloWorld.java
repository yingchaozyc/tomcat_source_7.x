package com.alibaba.tomcat.test.jmx;

public class HelloWorld implements HelloWorldMBean{

	@Override
	public String getNameIntroduce(String name) {
		return "fuck you ," + name + "!";
	}

	@Override
	public int getMyAge() {
		return 25;
	}

}
