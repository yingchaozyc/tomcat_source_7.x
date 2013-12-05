package com.alibaba.tomcat.test.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method; 
import java.util.Date;
 
class ReflectObject{
	private String name = "fuck";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 
}
public class ReflectTest {
	/**
	 * 简单的反射调用。
	 * 1. class.newInstance()
	 * 2. getMethod
	 * 3. method, invoke, which Object? what param
	 */
	public static void methodInvoke(){
		try {
			ReflectObject obj = ReflectObject.class.newInstance();
			Method method = ReflectObject.class.getMethod("setName", String.class);
			try { 
				method.invoke(obj, "ohyear");
				System.out.println(obj.getName());
			} catch (IllegalAccessException e) { 
				e.printStackTrace();
			} catch (IllegalArgumentException e) { 
				e.printStackTrace();
			} catch (InvocationTargetException e) { 
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) { 
			e.printStackTrace();
		} catch (SecurityException e) { 
			e.printStackTrace();
		} catch (InstantiationException e) { 
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 给null转型是可以的。
	 */
	public static void specialCast(){
		String[] arr = (String[])null;
		Date d = (Date)null;
		System.out.println(arr);
		System.out.println(d);
	}
	
	public static void main(String[] args) {
		methodInvoke(); 
		
		specialCast();
	}
}
