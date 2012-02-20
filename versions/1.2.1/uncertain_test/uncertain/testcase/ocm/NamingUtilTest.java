/**
 * Created on: 2004-6-11 13:31:20
 * Author:     zhoufan
 */
package uncertain.testcase.ocm;

import junit.framework.TestCase;
import uncertain.ocm.NamingUtil;
import junit.swingui.TestRunner;

/**
 * 
 */
public class NamingUtilTest extends TestCase {
	
	static String[] to_attrib_input = {"Attribute", "setM"};
	static String[] to_attrib_result = {"attribute", "setm"};	

	static String[] to_id_input = {"",  "*", "_", "A", "A-b", "_AB-cD"};
	static String[] to_id_result = {"", "", "_", "a", "ab", "_abcd"};	
	
	static String[] to_clsname_input = {"",  "a", "aa", "aAa", "aa**bb***cc***", "_abc-de", "element-name"};
	static String[] to_clsname_result = {"", "A", "Aa", "AAa", "AaBbCc", 		 "_abcDe", "ElementName"};	

	/**
	 * Constructor for NamingUtilTest.
	 * @param arg0
	 */
	public NamingUtilTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		TestRunner.run(NamingUtilTest.class);
	}

	public void testToAttribName() {
		for(int i=0; i<to_attrib_input.length; i++)
			assertEquals(NamingUtil.toAttribName(to_attrib_input[i]), to_attrib_result[i]);
	}

	public void testToIdentifier() {
		for(int i=0; i<to_id_input.length; i++)
			assertEquals(NamingUtil.toIdentifier(to_id_input[i]), to_id_result[i]);
	}

	public void testToClassName() {
		for(int i=0; i<to_clsname_input.length; i++)
			assertEquals(to_clsname_result[i], NamingUtil.toClassName(to_clsname_input[i]));
	}	

}
