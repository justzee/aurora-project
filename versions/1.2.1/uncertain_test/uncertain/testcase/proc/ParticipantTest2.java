/*
 * Created on 2005-6-3
 */
package uncertain.testcase.proc;
import uncertain.ocm.*;
import uncertain.proc.*;

/**
 * ParticipantTest2
 * @author Zhou Fan
 * 
 */
public class ParticipantTest2 extends ParticipantTest {
    
    public static final String RESULT = "RESULT";
    
    boolean		flag = true;
    
    public boolean getFlag(){
        return flag;
    }
    
    public void setFlag(boolean f){
        flag = f;
    }
    
    public ParticipantTest2(OCManager m){
        super(m);
    }
    
    // handle
    public void preActionA4(){
        checkPosition(3);
    }
    
    // handle
    public void onActionA4(){
        checkPosition(4);
    }
    
    // handle
    public void postActionA4(ProcedureRunner runner){
        checkPosition(5);
        ocManager.populateObject(runner.getContext(), this);
        runner.getContext().put(RESULT, this);
    }
    
    protected void onActionA2(){
        
    }
    
    

}
