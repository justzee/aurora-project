/*
 * Created on 2014年12月23日 下午8:41:54
 * $Id$
 */
package pipe.test;

import pipe.base.IEndPoint;

public class LogOutProcessor implements IEndPoint {

    public LogOutProcessor() {
        
    }

    @Override
    public void process(Object data) {
        LogRecord r = (LogRecord)data;
        r.content += " [processed by "+Thread.currentThread().getName()+"]";
        System.out.println(r.toString());
    }
    
    public void start(){
        
    }
    
    public void stop(){
        
    }
    

}
