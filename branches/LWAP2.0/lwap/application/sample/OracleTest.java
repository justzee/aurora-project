/**
 * Created on: 2002-11-11 12:08:59
 * Author:     zhoufan
 */
package org.lwap.application.sample;

import java.sql.Connection;

import oracle.jdbc.pool.OracleConnectionCache;

public class OracleTest {
	
	int finish_count = 0;
	int success_count = 0;
	double total_avg_time;
	int thread_count = 0;
	boolean is_all_done = false;
	
	public OracleTest( int tc){
		thread_count = tc;
	}
	
	public synchronized void addFinishThread( boolean success, double avg_time){
		finish_count ++;
		if( success){
 		  success_count++;
		  total_avg_time += avg_time;
		}  
		if( finish_count==thread_count) is_all_done = true;
	}
	
	public double getAvgTime(){
		return total_avg_time / success_count;
	}
	
	public int getSuccessCount(){
		return this.success_count;
	}
	
	public boolean isAllDone(){
		return this.is_all_done;
	}
	
	
	
	public class TestClass extends Thread {
		
		boolean is_success = false;
		
		int test_count = 2;
		int thread_id;
		long total_avg_time = 0;
		double avg_time = 0;
		OracleConnectionCache occi;
		
		public TestClass(OracleConnectionCache p, int id){
			 occi = p;
			 thread_id = id;
		}
		
		public void run(){			
	    long time = System.currentTimeMillis();
		for(int i=0;i<test_count; i++){
		 try{	
		  Connection conn = occi.getConnection();
		  conn.createStatement().executeUpdate("update hr_lbr_employee set employee_id = 665 where employee_id=665");		  
		  conn.commit();		  
		  conn.close();
		  total_avg_time += (System.currentTimeMillis() - time);
		  time = System.currentTimeMillis();
		 } catch(Exception ex){
		 	System.out.println(thread_id + ": "+ex);
		 	addFinishThread(false,0);
		 	return;
		 }
		}
		avg_time = total_avg_time / test_count;
	    System.out.println("Thread "+  thread_id +" avg time:"+ avg_time );	
		is_success = true;	
		addFinishThread(true,avg_time);
		}
		
	}
/*
	public static void main(String[] args) throws Exception {
		int thread_count = 100;
		TestClass[] threads = new TestClass[thread_count];
		OracleTest t = new OracleTest(thread_count);
		
		OracleConnectionCacheImpl occi = new OracleConnectionCacheImpl();
		try{
		    //occi.
			occi.setDriverType("thin");
			occi.setURL("jdbc:oracle:thin:@192.168.11.36:1521:hrms");		
			occi.setUser("hrms");
			occi.setPassword("hrms");
			occi.setMaxLimit(100);
			occi.setMinLimit(10);
			occi.setCacheScheme(OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME);
		
		    for( int n=0; n<thread_count; n++){
		   		threads[n] = t.new TestClass(occi,n);		   		
		   		threads[n].start();
		    }		    
		    
		    while( !t.isAllDone());
		    
		    System.out.println("Total success threads:"+ t.getSuccessCount());
		    System.out.println("Total avg time:"+ t.getAvgTime());
		    
		} finally{
			occi.close();
		}
	}
*/	
}
