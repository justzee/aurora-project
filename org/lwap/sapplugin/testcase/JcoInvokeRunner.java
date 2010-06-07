/*
 * Created on 2006-11-14
 */
package org.lwap.sapplugin.testcase;

import java.io.FileWriter;
import java.io.IOException;

public class JcoInvokeRunner extends Thread {
    
    int id;
    long run_tick = 0;
    
    static FileWriter out;
    static boolean stop = false;
    
    public JcoInvokeRunner(int id){
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
        try{
            sleep((long)(Math.random()*10000));
        }catch(Exception ex){
            
        }
        try{
            run_tick = System.currentTimeMillis();
            JCOInvokeTest t = new JCOInvokeTest("test");
            t.run();
            run_tick = System.currentTimeMillis() - run_tick;
            System.out.println("T"+id+":"+run_tick);
        }catch(Throwable ex){
            try{
                synchronized(out){
                    out.write(ex.getMessage());
                    out.write("\r\n");
                }
            }catch(IOException e){
                
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        try{
        out = new FileWriter("jcotest.log");
            while(!stop){
                for(int i=0; i<100; i++){
                    JcoInvokeRunner runner = new JcoInvokeRunner(i);            
                    runner.start();
                }
                //Thread.sleep(10000);
            }
        }finally{
            if(out!=null) out.close();
        }
    }

}
