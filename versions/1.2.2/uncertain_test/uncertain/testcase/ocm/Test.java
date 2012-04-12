/**
 * Created on: 2004-6-23 19:38:39
 * Author:     zhoufan
 */
package uncertain.testcase.ocm;

import uncertain.composite.*;

//import java.io.*;
import java.util.*;

import java.util.regex.*;
/**
 * 
 */
public class Test {
	
	public class ElementSet implements IterationHandle {

		public Set set = new HashSet();
		
		public int process(CompositeMap m){
			set.add(m.getName());
			return IterationHandle.IT_CONTINUE;
		}
		
		public List getElements(){
			List list = new LinkedList();
			list.addAll(set);
			Collections.sort(list);
			return list;
		}
		
	}

	/**
	 * Constructor for Test.
	 */
	public Test() {
		super();
	}
	
/*	
	public static void main(String[] args) throws Exception {
		Test t = new Test();
		ElementSet s = t.new ElementSet();
		CompositeMap root = CompositeMapParser.parse(new FileInputStream(new File("c:/HR/Web/service.xsd")));
		System.out.println(root.toXML());
		root.iterate(s, true);
		List l = s.getElements();
		Iterator it = l.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
*/
	public static void main(String[] args) throws Exception {
	    System.out.println("Matches:"+ Pattern.matches(".*\\.config","a.config"));
	    
	}

}
