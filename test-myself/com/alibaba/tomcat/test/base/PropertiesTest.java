package com.alibaba.tomcat.test.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesTest {
	/**
	 * 从InputStream读取到properties。
	 */
	public static void readFromInputstream(){
		PropertiesTest propertiesTest = new PropertiesTest();
		InputStream is = propertiesTest.getClass().getResourceAsStream("abc.properties");
		
		Properties properties = new Properties();
		try {
			properties.load(is);
			is.close();
		} catch (IOException e) {  
			e.printStackTrace();
		}
		
		System.out.println(properties);
		System.out.println("--------------------------------------------------------------");
	}
	
	/**
	 * 遍历Properties。
	 */
	public static void loopProperties(){
		PropertiesTest propertiesTest = new PropertiesTest();
		InputStream is = propertiesTest.getClass().getResourceAsStream("abc.properties");
		
		Properties properties = new Properties();
		try {
			properties.load(is);
			is.close();
		} catch (IOException e) {  
			e.printStackTrace();
		}
		
		Enumeration<?> enumObj = properties.propertyNames();
		while(enumObj.hasMoreElements()){
			String key = (String) enumObj.nextElement();
			String value = properties.getProperty(key);
			System.out.println("key:" + key + ", value:" + value);
		}
		System.out.println("--------------------------------------------------------------");
	}
	
	public static void main(String[] args) {
		readFromInputstream();
		
		loopProperties();
	}
}
