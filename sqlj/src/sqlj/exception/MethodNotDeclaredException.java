package sqlj.exception;

import sqlj.core.IProcedure;

public class MethodNotDeclaredException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4598557608994286835L;
	private IProcedure proc;
	private String methodName;
	
	public MethodNotDeclaredException(IProcedure proc,String methodName) {
		super("no public method :"+methodName+" is declared in proc :"+proc.getClass().getName());
		this.proc=proc;
		this.methodName=methodName;
	}
	
	public IProcedure getProcedure() {
		return proc;
	}
	
	public String getMethod() {
		return methodName;
	}
	

}
