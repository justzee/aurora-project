/*
 * Created on 2014年12月15日 下午11:52:16
 * $Id$
 */
package pipe.base;

public interface IEndPoint {
    
    public void process(Object data);
    
    public void start();
    
    public void stop();

}
