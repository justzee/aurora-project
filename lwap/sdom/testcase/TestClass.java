/*
 * TestClass.java
 *
 * Created on 2001年12月26日, 上午12:03
 */

package sdom.testcase;

import sdom.DOMNode;

/**
 *
 * @author  Administrator
 * @version 
 */
public class TestClass extends  DOMNode{

    String name;
    
    int value;
    
    public void set_name( String v){
        name = v;
    }
    
    public void set_value(String v){
        value = Integer.parseInt(v);
    }
    
    public String toString( int spc){
        return "<!-- TestClass Node -->\r\n" + super.toString(spc);
    }

}
