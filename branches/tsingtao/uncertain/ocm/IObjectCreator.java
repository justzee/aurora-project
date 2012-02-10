/*
 * Created on 2005-7-17
 */
package uncertain.ocm;


/**
 * IObjectCreator
 * @author Zhou Fan
 * 
 */
public interface IObjectCreator {
    
    public Object createInstance(Class cls)  throws Exception;

}
