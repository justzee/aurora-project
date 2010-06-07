/*
 * Created on 2008-11-5
 */
package org.lwap.action;

import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class Sleep extends AbstractEntry {
    
    public int  Time = 0;

    public void run(ProcedureRunner runner) throws Exception {
        //System.out.println("sleep "+Time);
        Thread.sleep(Time);
    }

}
