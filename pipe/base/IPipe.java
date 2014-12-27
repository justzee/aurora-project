/*
 * Created on 2014年12月16日 下午3:41:06
 * $Id$
 */
package pipe.base;

public interface IPipe {

    public void addFilter(IFilter filter);

    public boolean removeFilter(IFilter filter);

    public void addData(Object data);
    
    public void addData(Object data, IPipe return_pipe );

    //public T poll();

    public IEndPoint getEndPoint();

    public void setEndPoint(IEndPoint endPoint);

    public String getId();
    
    public void start();
    
    public void shutdown();

}