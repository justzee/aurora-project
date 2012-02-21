/*
 * Created on 2010-9-8 下午01:29:53
 * $Id$
 */
package aurora.testcase.service.validation;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import aurora.service.validation.CompositeParameterIterator;
import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;
import aurora.service.validation.Parameter;

public class CompositeParameterIteratorTest extends TestCase {
    
    static String[]  empty_params = new String[0];
    static String[]  param1 = {"P1"};    
    static String[]  param2 = {"P2", "P3"};
    static String[]  param3 = {"P4", "P5", "P6"};
    
    public static ParameterArrayIterator createParameterArray( String[] params ){
        IParameter[]    pa = new IParameter[params.length];
        for(int i=0; i<pa.length; i++)
            pa[i] = Parameter.createInputParameter(params[i], "java.lang.String");
        return new ParameterArrayIterator(pa);
    }

    public CompositeParameterIteratorTest(String name) {
        super(name);
    }
    
    private List getParameterNameList( IParameterIterator pi ){
        LinkedList lst = new LinkedList();
        while(pi.hasNext()){
            IParameter p = pi.next();
            lst.add(p.getName());
        }
        return lst;
    }

    public void testEmptyList(){
        LinkedList lst = new LinkedList();
        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 0);
    }
    
    public void testOneListWithEmpty(){
        LinkedList lst = new LinkedList();
        lst.add( createParameterArray(empty_params));
        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 0);        
    }

    public void testTwoListWithEmptyFirst(){
        LinkedList lst = new LinkedList();
        lst.add( createParameterArray(empty_params));
        lst.add( createParameterArray(param1) );
        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "P1");
    }
    
    public void testTwoListWithEmptyLast(){
        LinkedList lst = new LinkedList();
        lst.add( createParameterArray(param1) );
        lst.add( createParameterArray(empty_params));
        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "P1");        
    }
    
    private void checkParameterName(List result){
        for(int i=0; i<result.size(); i++){
            Object o = result.get(i);
            assertEquals("P"+(i+1), o);
        }        
    }
    
    public void testTwoList(){
        LinkedList lst = new LinkedList();
        lst.add( createParameterArray(param1) );
        lst.add( createParameterArray(param2));
        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 3);   
        checkParameterName(result);
    }
    
    public void testThreeList(){
        LinkedList lst = new LinkedList();
        lst.add( createParameterArray(param1) );
        lst.add( createParameterArray(param2));
        lst.add( createParameterArray(param3));
        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 6);       
        checkParameterName(result);
    }
    
    public void testThreeListWithEmptyFirstAndLast(){
        LinkedList lst = new LinkedList();
        lst.add( createParameterArray(empty_params) );
        lst.add( createParameterArray(param1) );
        lst.add( createParameterArray(param2));
        lst.add( createParameterArray(param3));
        lst.add( createParameterArray(empty_params) );

        CompositeParameterIterator pi = new CompositeParameterIterator(lst);
        List result = getParameterNameList(pi);
        assertEquals(result.size(), 6);       
        checkParameterName(result);

    }    

}
