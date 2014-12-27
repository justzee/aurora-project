/*
 * Created on 2014年12月23日 下午9:01:03
 * $Id$
 */
package pipe.test;

import java.util.Date;

import pipe.base.IPipe;

public class LogProducer extends Thread {
    
    IPipe   outputPipe;
    IPipe   returnPipe;

    public LogProducer(IPipe output, IPipe return_p) {
        super("LogProducer");
        this.outputPipe = output;
        this.returnPipe = return_p;
    }
    
    public void run(){
        while(!interrupted()){
            LogRecord r = new LogRecord( new Date(), "module"+(int)(Math.random()*100), " created info "+(int)(Math.random()*10000));
            outputPipe.addData(r, returnPipe);
            try{
                sleep(2);
            }catch(InterruptedException ex){
                break;
            }
        }
    }

}
