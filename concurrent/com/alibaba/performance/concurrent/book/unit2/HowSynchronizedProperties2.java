package com.alibaba.performance.concurrent.book.unit2;

public class HowSynchronizedProperties2 {
	public static void main(String[] args) {
		Cinema cinema = new Cinema();
		Thread t1 = new Thread(new TicketOffice1(cinema), "ticket-office-1");
		Thread t2 = new Thread(new TicketOffice2(cinema), "ticket-office-2");
		
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
			System.out.println("all thread are execute over.");
		} catch (InterruptedException e) { 
			e.printStackTrace();
		} 
		
		System.out.printf("Room 1 Vacancies: %d\n",cinema.getVacanciesCinema1()); 
		System.out.printf("Room 2 Vacancies: %d\n",cinema.getVacanciesCinema2());

	}
}

class Cinema{
	 private long vacanciesCinema1;
	 
	 private long vacanciesCinema2;
	 
	 // Lock Object 
	 private final Object controlCinema1, controlCinema2;

	 public Cinema(){
		 controlCinema1 = new Object();
		 controlCinema2 = new Object();
		 vacanciesCinema1 = 20;
		 vacanciesCinema2 = 20;
	 }
	 
	 public boolean sellTickets1 (int number) { 
		 if (number<vacanciesCinema1) { 
			 vacanciesCinema1-=number; 
			 return true; 	
		 } else { 
			 return false; 
		 }  
	 }
	 
	 public boolean sellTickets2 (int number) { 
		 if (number<vacanciesCinema2) { 
			 vacanciesCinema2-=number; 
			 return true; 	
		 } else { 
			 return false; 
		 }  
	 }
	 
	 public boolean returnTickets1 (int number) {  
		 vacanciesCinema1+=number; 
		 return true;  
	 }

	 public boolean returnTickets2 (int number) {  
		 vacanciesCinema2+=number; 
		 return true;  
	 }
	 
	 public long getVacanciesCinema1() {
		 return vacanciesCinema1;
	 }
	 
	 public long getVacanciesCinema2() { 
		 return vacanciesCinema2; 
	 } 
}

class TicketOffice1 implements Runnable{

	private Cinema cinema;
	
	public TicketOffice1(Cinema cinema){
		this.cinema = cinema;
	}
	
	@Override
	public void run() {
		cinema.sellTickets1(3); 
		cinema.sellTickets1(2); 
		cinema.sellTickets2(2); 
		cinema.returnTickets1(3); 
		cinema.sellTickets1(5); 
		cinema.sellTickets2(2); 
		cinema.sellTickets2(2); 
		cinema.sellTickets2(2); 
	} 
}

class TicketOffice2 implements Runnable{

	private Cinema cinema;
	
	public TicketOffice2(Cinema cinema){
		this.cinema = cinema;
	}
	
	@Override
	public void run() {
		cinema.sellTickets1(2); 
		cinema.sellTickets1(4); 
		cinema.sellTickets2(1); 
		cinema.returnTickets1(3); 
		cinema.sellTickets1(1); 
		cinema.sellTickets2(2); 
		cinema.sellTickets2(5); 
		cinema.sellTickets2(2); 
	} 
}












