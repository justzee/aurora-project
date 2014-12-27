/*
 * Created on 2014年12月23日 下午10:16:36
 * $Id$
 */
package pipe.test;

import java.util.Date;

import pipe.base.IPipe;
import pipe.simple.SimplePipe;

public class Main {

    public Main() throws Exception {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws Exception {
        
        IPipe   input = new SimplePipe("log.process", 4);
        input.setEndPoint( new LogOutProcessor());
        input.start();
        
        IPipe output = new SimplePipe("log.writefile", 2);
        output.setEndPoint( new LogReturnProcessor());
        output.start();
        
        LogProducer producer = new LogProducer(input, output);
        producer.start();

        Thread.sleep(1000*1);
        System.out.println("Step.1 productor stop "+ new Date());
        producer.interrupt();

        Thread.sleep(1000*10);
        
        input.shutdown();
        System.out.println("Step.2 input stop "+ new Date());
        output.shutdown();
        System.out.println("Step.3 output stop "+ new Date());

    }

}
