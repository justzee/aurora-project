/*
 * Created on 2007-6-5
 */
package uncertain.testcase.object;

import uncertain.proc.ProcedureRunner;

public class TestExceptionHandle {
    
    public static final String KEY_EXCEPTION = "exception";
    
    public void onRaiseException(ProcedureRunner runner) throws Exception {
        String exp = runner.getContext().getString(KEY_EXCEPTION);
        Exception ex = (Exception)(Class.forName(exp).newInstance());
        throw ex;
    }
    
    
    public void onDoRight( ProcedureRunner runner ){
        runner.getContext().put("success", new Boolean(true));
    }
    /*
    public boolean handleException(ProcedureRunner runner, Throwable exception){
        if(exception instanceof IllegalArgumentException){
            runner.locateTo("DoRight");
            return true;
        }
        return false;
    }
    */
    
    public void onAnyException(ProcedureRunner runner){
        runner.getContext().put("any", new Boolean(true));
    }
    
    public void onSQLException(ProcedureRunner runner) throws Exception {        
        throw new IllegalAccessException();
    }

    public void onUnExpectedAction(ProcedureRunner runner){
        runner.getContext().put("unexpect", new Boolean(true));
        throw new IllegalStateException();
    }

}
