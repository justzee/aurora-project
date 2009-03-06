/**
 * Created on: 2004-6-15 17:13:00
 * Author:     zhoufan
 */
package uncertain.testcase.object;

/**
 * 
 */
public class Name {

	String	FirstName;
	String  LastName;
	
	public Name(){
	    super();
	}
	
	public Name(String f, String l){
	    FirstName = f;
	    LastName = l;
	}
	
	public String toString(){
		return FirstName+","+LastName;
	}

}
