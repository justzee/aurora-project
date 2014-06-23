package sqlj.core;


public interface IProcedure {
	
	void __init__(IContext context);
	void __finallize__();
	
	IContext getContext();

}
