/*
 * Created on 2005-6-3
 */
package uncertain.testcase.proc;
import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;

/**
 * ParticipantTest
 * @author Zhou Fan
 * 
 */
public class ParticipantTest {
    
    
    OCManager ocManager;
    
    public ParticipantTest(OCManager ocManager){
        this.ocManager = ocManager;
    }
    
    int position = 0;    
    
    public void checkPosition(int p){
        //System.out.println("[participant] "+getClass().getName()+" invoke at "+position);
        if(position!=p) throw new IllegalStateException("wrong position:expected "+p+", but got "+position);
        position++;
    }
    
    // handle
    public int preActionA5(){
        return 0;
    }
    
    // handle
    public void onActionA5(ProcedureRunner r, String a2){
        
    }
    
    // handle
    public void preActionA1(ProcedureRunner runner){
        checkPosition(0);
        CompositeMap context = runner.getContext();
        //context.put("flag", new Boolean(false));
        context.put("flag", "false");
    }
    
    // not handle
    public void on(){
        
    }
    
    // not handle
    public void pre(ProcedureRunner r){
        
    }

    // not handle, 'on' should be lower case
    public void OnACTIONA1(){
        
    }
    
    // handle
    public void onACTIONA2(){
        checkPosition(1);
    }
    
    // not handle, first char after pre is not upper case
    public void preactionA2(){
        
    }
    
    // handle
    public void postActionA2(){
        checkPosition(2);
        //throw new IllegalArgumentException();
    }
    
    // not handle, return type is not void
    public double preActionC1(){
        return 0;
    }
    
    public int getPosition(){
        return position;
    }

}
