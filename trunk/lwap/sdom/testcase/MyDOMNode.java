/*
 * MyDOMNode.java
 *
 * Created on 2001年9月19日, 上午3:29
 */

package sdom.testcase;
import sdom.DOMNode;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class MyDOMNode extends DOMNode {
    
    String name;
    
    public void set_Name( String n){
          name = n;
    }
    
    public void SetName( String n){
          name = n;
    }
    
    public String get_Name(){
          return name;
    }
    
    public String toString(int space){
    	return "<!-- MyDOMNode -->\r\n"+super.toString(space);
    	}

    /** Creates new MyDOMNode */
    public MyDOMNode() {
        super();
    }

    public static void main(String[] args){
      System.out.println("MyDOMNode");
    }
}
