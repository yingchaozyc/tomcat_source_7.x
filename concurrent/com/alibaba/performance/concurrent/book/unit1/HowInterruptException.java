package com.alibaba.performance.concurrent.book.unit1;

import java.io.File;

public class HowInterruptException {
	public static void main(String[] args) {
		Thread t = new Thread(new Fucker("c:/", "AliPaySE.dll"));
		
		t.start();
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		
		t.interrupt();
	}
}

class Fucker implements Runnable{
	
	private String initPath;
	private String fileName;
	 
	Fucker(String initPath, String fileName) { 
		this.initPath = initPath;
		this.fileName = fileName;
	}
 
	@Override
	public void run() {
		File file = new File(initPath);
		
		try {
			processDir(file);
		} catch (InterruptedException e) {
			System.out.println("卧槽我被中断了！");
		}
	}
	
	public void processDir(File file) throws InterruptedException{
		File[] files = file.listFiles();
		if(files != null){
			for(File f : files){
				if(f.isDirectory()){
					processDir(f);
				} else {
					processFile(f);
				}
			}
		}
		
		if(Thread.interrupted()){
			throw new InterruptedException();
		}
	}
	
	public void processFile(File file) throws InterruptedException{
		if(file.getName().equals(fileName)){ 
			System.out.println("=========>find it," + file.getAbsolutePath() + "<===================");
		} else {
			System.out.println(file.getAbsolutePath());
		}
		
		if(Thread.interrupted()){
			throw new InterruptedException();
		}
	}
}




