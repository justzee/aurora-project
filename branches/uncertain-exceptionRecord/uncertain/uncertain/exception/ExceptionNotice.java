package uncertain.exception;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import uncertain.core.UncertainEngine;

public class ExceptionNotice {
	UncertainEngine  uncertainEngine;
	List exceptionListener = new LinkedList();
	public ExceptionNotice(UncertainEngine uncertainEngine){
		this.uncertainEngine = uncertainEngine;
	}
	public void addListener(IExceptionListener listener){
		if(listener != null)
			exceptionListener.add(listener);
	}
	public void removeListener(IExceptionListener listener){
		if(listener != null){
			if(exceptionListener.contains(listener)){
				exceptionListener.remove(listener);
			}
		}
	}
	public void notice(Throwable e){
		if(e == null)
			return;
		IExceptionListener listener = null;
		for(Iterator it = exceptionListener.iterator();it.hasNext();){
			try{
				listener = ((IExceptionListener)exceptionListener);
				listener.onException(e);
			}catch (Exception ex) {
				uncertainEngine.logException("Error when notice exception:"+ listener.getClass().getName(), ex);
			}
		}
	}
}
