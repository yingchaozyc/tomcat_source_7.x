package com.alibaba.performance.concurrent.book.unit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HowThreadPoolCallableInvokeAny {
	public static void main(String[] args) {
		String username = "DAVID";
		
		String password = "beckham";
		
		UserValidator ldapValidate = new UserValidator("ldap");
		
		UserValidator dbValidate = new UserValidator("db");
		
		Task ldapTask = new Task(ldapValidate, username, password);
		Task dbTask = new Task(dbValidate, username, password);
		
		List<Task> taskList = new ArrayList<Task>();
		taskList.add(ldapTask);
		taskList.add(dbTask);
		
		ExecutorService executor = Executors.newCachedThreadPool();
		String result = null;
		
		try {
			result = executor.invokeAny(taskList);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} catch (ExecutionException e) { 
			e.printStackTrace();
		}
		System.out.println("result is:" + result);
	}
}

class UserValidator {
	private String name;
	
	public UserValidator(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean validate(String username, String password){
		Random random=new Random(); 
		try { 
			long duration=(long)(Math.random()*10); 
			System.out.printf("Validator %s: Validating a user during %d seconds\n",this.name,duration);
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			return false;
		}

		return random.nextBoolean();
	}
}

class Task implements Callable<String>{

	private UserValidator userValidator;
	
	private String username;
	
	private String password;
	  
	public Task(UserValidator userValidator, String username, String password) { 
		this.userValidator = userValidator;
		this.username = username;
		this.password = password;
	}
 
	@Override
	public String call() throws Exception {
		if(!userValidator.validate(username, password)){
			System.out.printf("%s: The user has not been found\n",userValidator.getName());
			throw new Exception("Error validating user"); 
		}
		
		System.out.printf("%s: The user has been found\n",userValidator.getName());
		return userValidator.getName(); 
	}
	
}







